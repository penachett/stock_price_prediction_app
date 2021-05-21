package main

type errorResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

type success struct {
	Success bool `json:"success"`
}

type user struct {
	Id int `json:"id"`
}

type prediction struct {
	Id int `json:"id"`
	Ticker string `json:"ticker"`
	CreateTime string `json:"create_time"`
	PredictTime string `json:"predict_time"`
	PredictedPrice string `json:"predicted_price"`
	UserId string `json:"user_id"`
}

type infoForPrediction struct {
	Ticker string `json:"ticker"`
	ClosePrices []int `json:"close_prices"`
}