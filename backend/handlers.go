package main

import (
	"encoding/json"
	"net/http"
)

func handleRegister(w http.ResponseWriter, r *http.Request) {
	send, _ := json.Marshal(success{Success:true})
	writeBytes(w, &send, 200)
}

func handleLogin(w http.ResponseWriter, r *http.Request) {

}

func handleMakePrediction(w http.ResponseWriter, r *http.Request) {

}

func handleSavePrediction(w http.ResponseWriter, r *http.Request) {

}

func handleDeletePrediction(w http.ResponseWriter, r *http.Request) {

}

func handleGetPredictions(w http.ResponseWriter, r *http.Request) {

}
