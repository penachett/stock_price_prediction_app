package main

import (
	"database/sql"
	"errors"
	"fmt"
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


