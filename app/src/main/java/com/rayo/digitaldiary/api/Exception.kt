package com.rayo.digitaldiary.api

import java.io.IOException


class ApiException(message: String) : IOException(message)

class NoInternetException(message: String) : IOException(message)

class UnauthorizedUserException(message: String) : IOException(message)