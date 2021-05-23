package main

type errorResponse struct {
	Code    int    `json:"code"`
	Message string `json:"message"`
}

type success struct {
	Success bool `json:"success"`
}

type user struct {
	Id int64 `json:"id"`
}

type prediction struct {
	Id             int64   `json:"id"`
	Ticker         string  `json:"ticker"`
	CreateTime     int64   `json:"create_time"`
	PredictTime    int64   `json:"predict_time"`
	PredictedPrice float64 `json:"predicted_price"`
	StartPrice     float64 `json:"start_price"`
	UserId         int64   `json:"user_id"`
}

type infoForPrediction struct {
	Ticker      string    `json:"ticker"`
	ClosePrices []float64 `json:"close_prices"`
}
