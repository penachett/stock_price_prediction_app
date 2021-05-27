package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"
	"strings"
	"time"
)

func handleRegister(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		sendError(405, "", w)
		return
	}
	if err := r.ParseForm(); err != nil {
		sendError(400, err.Error(), w)
		return
	}
	login := r.PostFormValue("login")
	if err := validateLogin(login); err != nil {
		sendError(400, err.Error(), w)
		return
	}
	password := r.PostFormValue("password")
	if err := validatePassword(password); err != nil {
		sendError(400, err.Error(), w)
		return
	}
	token, err := registerUser(login, password)
	if err != nil {
		sendError(400, err.Error(), w)
		return
	}
	http.SetCookie(w, &http.Cookie{
		Name:    AUTH_COOKIE_NAME,
		Value:   token,
		Expires: time.Now().Add(time.Hour * COOKIE_LIFE_DAYS * 24)})
	w.Header().Set("Content-Type", "application/json")
	send, err := json.Marshal(success{Success: true})
	if err != nil {
		sendError(500, "error marshalling: "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}

func handleLogin(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		sendError(405, "", w)
		return
	}
	if err := r.ParseForm(); err != nil {
		sendError(400, err.Error(), w)
		return
	}
	login := r.PostFormValue("login")
	if err := validateLogin(login); err != nil {
		sendError(400, err.Error(), w)
		return
	}
	password := r.PostFormValue("password")
	if err := validatePassword(password); err != nil {
		sendError(400, err.Error(), w)
		return
	}
	token, err := loginUser(login, password)
	if err != nil {
		sendError(400, err.Error(), w)
		return
	}
	fmt.Println("generated token: " + token)
	http.SetCookie(w, &http.Cookie{
		Name:    AUTH_COOKIE_NAME,
		Value:   token,
		Expires: time.Now().Add(time.Hour * COOKIE_LIFE_DAYS * 24)})
	w.Header().Set("Content-Type", "application/json")
	send, err := json.Marshal(success{Success: true})
	if err != nil {
		sendError(500, "error marshalling: "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}

func handleMakePrediction(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		sendError(405, "", w)
		return
	}
	cookie, err := r.Cookie(AUTH_COOKIE_NAME)
	if err != nil {
		if err == http.ErrNoCookie {
			sendError(401, "", w)
			return
		}
		sendError(400, err.Error(), w)
		return
	}
	user, authorized, err := checkAuth(cookie.Value)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	if !authorized {
		sendError(401, "", w)
		return
	}
	pricesStr := r.PostFormValue("prices")
	ticker := r.PostFormValue("ticker")
	predictMonthsStr := r.PostFormValue("predict_months")
	if pricesStr == "" || ticker == "" || predictMonthsStr == "" {
		sendError(400, "prices/ticker/predict_month required", w)
		return
	}
	predictMonths, err := strconv.Atoi(predictMonthsStr)
	if err != nil {
		sendError(400, "wrong predict_months", w)
		return
	}
	if predictMonths != 1 && predictMonths != 3 && predictMonths!= 6 {
		sendError(400, "predict_months can be 1/3/6", w)
		return
	}
	var prices []float64
	err = json.Unmarshal([]byte(pricesStr), &prices)
	if err != nil {
		sendError(400, "wrong prices", w)
		return
	}
	predictedPrice, err := makePrediction(prices, predictMonths, strings.ToLower(ticker))
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	prediction := new(prediction)
	prediction.PredictedPrice = predictedPrice
	curTime := time.Now()
	prediction.PredictTime = curTime.AddDate(0, predictMonths, 0).Unix()
	prediction.CreateTime = curTime.Unix()
	prediction.Ticker = ticker
	prediction.UserId = user.Id
	w.Header().Set("Content-Type", "application/json")
	send, err := json.Marshal(prediction)
	if err != nil {
		sendError(500, "error marshalling "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}

func handleSavePrediction(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		sendError(405, "", w)
		return
	}
	cookie, err := r.Cookie(AUTH_COOKIE_NAME)
	if err != nil {
		if err == http.ErrNoCookie {
			sendError(401, "", w)
			return
		}
		sendError(400, err.Error(), w)
		return
	}
	user, authorized, err := checkAuth(cookie.Value)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	if !authorized {
		sendError(401, "", w)
		return
	}
	ticker := r.PostFormValue("ticker")
	createTimeStr := r.PostFormValue("create_time")
	predictTimeStr := r.PostFormValue("predict_time")
	predictedPriceStr := r.PostFormValue("predicted_price")
	startPriceStr := r.PostFormValue("start_price")
	if createTimeStr == "" || ticker == "" || predictTimeStr == "" || predictedPriceStr == "" || startPriceStr == "" {
		sendError(400, "ticker/create_time/predict_time/predicted_price/start_price required", w)
		return
	}
	createTime, err := strconv.ParseInt(createTimeStr, 10, 64)
	if err != nil {
		sendError(400, "wrong create_time", w)
		return
	}
	predictTime, err := strconv.ParseInt(predictTimeStr, 10, 64)
	if err != nil {
		sendError(400, "wrong predict_time", w)
		return
	}
	predictedPrice, err := strconv.ParseFloat(predictedPriceStr, 10)
	if err != nil {
		sendError(400, "wrong predicted_price", w)
		return
	}
	startPrice, err := strconv.ParseFloat(startPriceStr, 10)
	if err != nil {
		sendError(400, "wrong start_price", w)
		return
	}
	if createTime > predictTime {
		sendError(400, "create_time bigger than predict_time", w)
		return
	}
	prediction := new(prediction)
	prediction.PredictedPrice = predictedPrice
	prediction.StartPrice = startPrice
	prediction.PredictTime = predictTime
	prediction.CreateTime = createTime
	prediction.Ticker = ticker
	prediction.UserId = user.Id
	predictionId, err := savePrediction(prediction)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	prediction.Id = predictionId
	w.Header().Set("Content-Type", "application/json")
	send, err := json.Marshal(prediction)
	if err != nil {
		sendError(500, "error marshalling "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}

func handleDeletePrediction(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		sendError(405, "", w)
		return
	}
	cookie, err := r.Cookie(AUTH_COOKIE_NAME)
	if err != nil {
		if err == http.ErrNoCookie {
			sendError(401, "", w)
			return
		}
		sendError(400, err.Error(), w)
		return
	}
	user, authorized, err := checkAuth(cookie.Value)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	if !authorized {
		sendError(401, "", w)
		return
	}
	idStr := r.PostFormValue("prediction_id")
	if idStr == "" {
		sendError(400, "prediction_id required", w)
		return
	}
	id, err := strconv.ParseInt(idStr, 10, 64)
	if err != nil {
		sendError(400, "wrong prediction_id", w)
		return
	}
	err = deletePrediction(user.Id, id)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	send, err := json.Marshal(success{Success: true})
	if err != nil {
		sendError(500, "error marshalling "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}

func handleGetPredictions(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		sendError(405, "", w)
		return
	}
	cookie, err := r.Cookie(AUTH_COOKIE_NAME)
	if err != nil {
		if err == http.ErrNoCookie {
			sendError(401, "", w)
			return
		}
		sendError(400, err.Error(), w)
		return
	}
	user, authorized, err := checkAuth(cookie.Value)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	if !authorized {
		sendError(401, "", w)
		return
	}
	predictions, err := getPredictions(user.Id)
	if err != nil {
		sendError(500, err.Error(), w)
		return
	}
	reverseSlice(predictions)
	w.Header().Set("Content-Type", "application/json")
	send, err := json.Marshal(predictions)
	if err != nil {
		sendError(500, "error marshalling "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}
