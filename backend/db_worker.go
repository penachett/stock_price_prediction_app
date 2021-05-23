package main

import (
	"database/sql"
	"errors"
	"fmt"
	_ "github.com/lib/pq"
	"time"
)

const (
	connStr = "user=penachett password=stonksapp908365 dbname=stonks sslmode=disable"
)

var db *sql.DB

func initDB() error {
	for i := 0; i < 5; i++ {
		var err error
		db, err = sql.Open("postgres", connStr)
		if err != nil {
			fmt.Println("sql open: " + err.Error())
		} else {
			return nil
		}
	}
	return errors.New("failed to connect after 5 attempts")
}

func checkUserExist(login string) (bool, error) {
	row := db.QueryRow("select login from users where login = $1", login)
	var tmpStr string
	err := row.Scan(&tmpStr)
	if err != nil {
		if err == sql.ErrNoRows {
			return false, nil
		}
		fmt.Println("check user exist: " + err.Error())
		return false, err
	} else {
		return true, nil
	}
}

func checkAuth(token string) (*user, bool, error) {
	curDate := "'" + time.Now().Format("2006-01-02") + "'"
	row := db.QueryRow("select id from users where token = $1 and token_expire >= $2::date", token, curDate)
	user := new(user)
	err := row.Scan(&user.Id)
	if err != nil {
		if err == sql.ErrNoRows {
			return user, false, nil
		}
		fmt.Println("check auth: " + err.Error())
		return user, false, err
	} else {
		return user, true, nil
	}
}

func registerUser(login, password string) (string, error) {
	exist, err := checkUserExist(login)
	if err != nil {
		fmt.Println("Registration: " + err.Error())
		return "", err
	}
	if exist {
		return "", errors.New(ERR_USER_ALREADY_EXIST)
	}
	passHash, err := hashBcrypt(password)
	if err != nil {
		fmt.Println(err.Error())
		return "", err
	}
	token := getNewRandomToken()
	exprDate := "'" + time.Now().AddDate(0, 0, COOKIE_LIFE_DAYS).Format("2006-01-02") + "'"
	_, err = db.Exec("insert into users(login, password, token, token_expire) values($1, $2, $3, $4)", login, passHash, token, exprDate)
	if err != nil {
		fmt.Println("Registration: " + err.Error())
		return "", err
	}
	return token, nil
}

func loginUser(login string, password string) (string, error) {
	exist, err := checkUserExist(login)
	if err != nil {
		fmt.Println("Login: " + err.Error())
		return "", err
	}
	if !exist {
		return "", errors.New(ERR_USER_NOT_EXIST)
	}
	row := db.QueryRow("select password from users where login = $1", login)
	var passHash string
	err = row.Scan(&passHash)
	if err != nil {
		return "", err
	}
	passwordOk := checkHash(password, passHash)
	if !passwordOk {
		return "", errors.New(ERR_WRONG_PASSWORD)
	}
	token := getNewRandomToken()
	exprDate := "'" + time.Now().AddDate(0, 0, COOKIE_LIFE_DAYS).Format("2006-01-02") + "'"
	_, err = db.Exec("update users set token = $1, token_expire = $2 where login = $3", token, exprDate, login)
	if err != nil {
		fmt.Println("Login: " + err.Error())
		return "", err
	}
	return token, nil
}

func getPredictions(userId int64) ([]prediction, error) {
	res := make([]prediction, 0)
	queryStr := "select * from predictions where user_id = $1"
	rows, err := db.Query(queryStr, userId)
	if err != nil {
		if err == sql.ErrNoRows {
			return res, nil
		} else {
			fmt.Println("select tests: ", err.Error())
			return nil, err
		}
	}
	defer rows.Close()
	var prediction prediction
	for rows.Next() {
		err := rows.Scan(&prediction.Id, &prediction.Ticker, &prediction.CreateTime, &prediction.PredictTime, &prediction.PredictedPrice)
		if err != nil {
			fmt.Println("select predictions loop: ", err.Error())
			return nil, err
		}
		res = append(res, prediction)
	}
	if err = rows.Err(); err != nil {
		fmt.Println("select predictions final: ", err.Error())
		return nil, err
	}
	return res, nil
}

func checkUserPredictionAccess(userId, predictionId int64) (bool, error) {
	row := db.QueryRow("select user_id from predcitions where id = $1", predictionId)
	var ownerId int64
	err := row.Scan(&ownerId)
	if err != nil {
		if err == sql.ErrNoRows {
			return false, nil
		}
		fmt.Println("check user prediction access: " + err.Error())
		return false, err
	} else {
		return userId == ownerId, nil
	}
}

func deletePrediction(userId, predictionId int64) error {
	hasAccess, err := checkUserPredictionAccess(userId, predictionId)
	if err != nil {
		return err
	}
	if !hasAccess {
		return errors.New("no predictions with provided id")
	}
	_, err = db.Exec("delete from predictions where id = $1", predictionId)
	if err != nil {
		return err
	}
	return nil
}
