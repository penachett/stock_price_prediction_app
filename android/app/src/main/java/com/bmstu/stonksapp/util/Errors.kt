package com.bmstu.stonksapp.util

const val ERR_SHORT_LOGIN = "login length must be >= 4 characters"
const val ERR_SHORT_PASSWORD = "password length must be >= 8 characters"
const val ERR_USER_ALREADY_EXIST = "user already exist"
const val ERR_USER_NOT_EXIST = "user not exist"
const val ERR_WRONG_CREDENTIALS = "wrong password"
const val ERR_MARSHALLING = "error marshalling"

const val DEFAULT_ERR_MESSAGE = "Произошла ошибка. Пожалуйста, проверьте соединение и попробуйте снова"

fun errToMessage(err: String?): String {
    if (err == null) {
        return DEFAULT_ERR_MESSAGE
    }
    return when(err) {
        ERR_SHORT_LOGIN -> "Минимальная длина логина составляет 4 символа"
        ERR_SHORT_PASSWORD -> "Минимальная длина пароля составляет 8 символов"
        ERR_USER_ALREADY_EXIST -> "Данный логин уже используется"
        ERR_USER_NOT_EXIST -> "Пользователя с данным логином не существует"
        ERR_WRONG_CREDENTIALS -> "Введены неверные данные"
        else -> DEFAULT_ERR_MESSAGE
    }
}
