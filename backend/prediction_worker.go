package main

import (
	"bufio"
	"fmt"
	"io/ioutil"
	"os/exec"
	"strconv"
)

func makePrediction(prices []float64, predictMonths int, ticker string) (float64, error) {
	err := writeInput(prices, predictMonths, ticker)
	if err != nil {
		return 0, err
	}
	cmd := exec.Command("python3", "../ml/launcher.py")
	stdout, err := cmd.StdoutPipe()
	if err != nil {
		return 0, err
	}
	err = cmd.Start()
	if err != nil {
		return 0, err
	}

	scanner := bufio.NewScanner(stdout)
	for scanner.Scan() {
		fmt.Println(scanner.Text())
		break
	}
	if err := cmd.Wait(); err != nil {
		return 0, err
	}
	res, err := readResult()
	if err != nil {
		return 0, err
	}
	return res, nil
}

func writeInput(prices []float64, predictMonths int, ticker string) error {
	pricesStr := ""
	for i := 0; i < len(prices); i++ {
		pricesStr += fmt.Sprintf("%f", prices[i])
		if i != len(prices)-1 {
			pricesStr += " "
		}
	}
	bytes := []byte(ticker + "\n" + fmt.Sprintf("%d", predictMonths) + "\n" + pricesStr)
	err := ioutil.WriteFile("../ml/input.txt", bytes, 0644)
	if err != nil {
		return err
	}
	return nil
}

func readResult() (float64, error) {
	resStr, err := ioutil.ReadFile("../ml/output.txt")
	if err != nil {
		return 0, err
	}
	res, err := strconv.ParseFloat(string(resStr), 64)
	if err != nil {
		return 0, err
	}
	return res, nil
}
