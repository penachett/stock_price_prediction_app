package main

import (
	"fmt"
	"net/http"
	"strconv"
)

const (
	port = 9990
)

func main() {
	http.HandleFunc("/api/register", handleRegister)
	http.HandleFunc("/api/login", handleLogin)
	http.HandleFunc("/api/make_prediction", handleMakePrediction)
	http.HandleFunc("/api/save_prediction", handleSavePrediction)
	http.HandleFunc("/api/delete_prediction", handleDeletePrediction)
	http.HandleFunc("/api/get_predictions", handleGetPredictions)
	fmt.Println("initiating db")
	if err := initDB(); err != nil {
		panic(err)
	}
	fmt.Println("starting server on " + strconv.Itoa(port) + " port")
	err := http.ListenAndServe(":" + strconv.Itoa(port), nil)
	if err != nil {
		panic(err)
	}
}
