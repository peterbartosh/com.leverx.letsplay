package app.init

import data.service.JwtService
import data.tables.tokens.TokensDao
import model.entity.TokensEntity

suspend fun TokensDao.addSomeData(jwtService: JwtService){
    this.addTokens(
        tokensEntity = TokensEntity(
            userId = 1,
            accessToken = jwtService.createAccessToken(),
            refreshToken = jwtService.createRefreshToken(),
            accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
            refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
        )
    )
    this.addTokens(
        tokensEntity = TokensEntity(
            userId = 2,
            accessToken = jwtService.createAccessToken(),
            refreshToken = jwtService.createRefreshToken(),
            accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
            refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
        )
    )
    this.addTokens(
        tokensEntity = TokensEntity(
            userId = 3,
            accessToken = jwtService.createAccessToken(),
            refreshToken = jwtService.createRefreshToken(),
            accessTokenExpiresIn = jwtService.accessTokenExpiresIn(),
            refreshTokenExpiresIn = jwtService.refreshTokenExpiresIn()
        )
    )

}