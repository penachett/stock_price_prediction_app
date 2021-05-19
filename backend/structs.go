package main

type errorResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

type success struct {
	Success bool `json:"success"`
}