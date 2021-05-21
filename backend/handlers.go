package main

import (
	"encoding/json"
	"fmt"
	"net/http"
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
	send, err := json.Marshal(success{Success: true})
	if err != nil {
		sendError(500, "error marshalling: "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}

func handleMakePrediction(w http.ResponseWriter, r *http.Request) {

}

func handleSavePrediction(w http.ResponseWriter, r *http.Request) {

}

func handleDeletePrediction(w http.ResponseWriter, r *http.Request) {

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
	w.Header().Set("Content-Type", "application/json")
	send, err := json.Marshal(predictions)
	if err != nil {
		sendError(500, "error marshalling "+err.Error(), w)
		return
	}
	writeBytes(w, &send, 200)
}
