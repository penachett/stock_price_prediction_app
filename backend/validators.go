package main

import "errors"

func validateLogin(login string) error {
	if len(login) < 4 {
		return errors.New("login length must be >= 4 characters")
	} else {
		return nil
	}
}

func validatePassword(password string) error {
	if len(password) < 8 {
		return errors.New("password length must be >= 8 characters")
	} else {
		return nil
	}
}
