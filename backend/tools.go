package main

import (
	"golang.org/x/crypto/bcrypt"
	"math/rand"
	"time"
)

func getNewRandomToken() string {
	rand.Seed(time.Now().UnixNano())
	token := ""
	for i := 0; i < TOKEN_LEN; i++ {
		pos := rand.Intn(len(ALPHABET))
		token += string(ALPHABET[pos])
	}
	return token
}

func hashBcrypt(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), 14)
	return string(bytes), err
}

func checkHash(password, hash string) bool {
	err := bcrypt.CompareHashAndPassword([]byte(hash), []byte(password))
	return err == nil
}

func reverseSlice(a []prediction) {
	for i, j := 0, len(a)-1; i < j; i, j = i+1, j-1 {
		a[i], a[j] = a[j], a[i]
	}
}
