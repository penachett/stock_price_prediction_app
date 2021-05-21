package main

import (
	"fmt"
	"net/http"
)

func main() {
	http.HandleFunc("/api/register", handleRegister)
	http.HandleFunc("/api/login", handleLogin)
	http.HandleFunc("/api/make_prediction", handleMakePrediction)
	http.HandleFunc("/api/save_prediction", handleSavePrediction)
	http.HandleFunc("/api/delete_prediction", handleDeletePrediction)
	http.HandleFunc("/api/get_predictions", handleGetPredictions)
	fmt.Println("starting server")
	err := http.ListenAndServe(":9990", nil)
	if err != nil {
		panic(err)
	}
}
