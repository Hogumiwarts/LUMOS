const express = require('express');
const router = express.Router();
const { getAuthUrl, oauthCallback } = require('../controllers/auth.controller');

/**
 * @swagger
 * tags:
 *   - name: OAuth
 *     description: 인증 관련 API
 */

/**
 * @swagger
 * /oauth/url:
 *   get:
 *     summary: SmartThings OAuth 인증 URL 요청
 *     description: 클라이언트가 SmartThings 로그인 창으로 이동할 수 있도록 OAuth 인증 URL을 반환합니다.
 *     tags: [OAuth]
 *     responses:
 *       200:
 *         description: 인증 URL이 성공적으로 반환됨
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 url:
 *                   type: string
 *                   example: "https://api.smartthings.com/oauth/authorize?client_id=...&redirect_uri=...&response_type=code"
 *       500:
 *         description: 서버 오류로 인해 인증 URL을 생성하지 못함
 */
router.get('/url', getAuthUrl);

/**
 * @swagger
 * /oauth/callback:
 *   get:
 *     summary: SmartThings OAuth 인증 완료 콜백
 *     description: SmartThings 로그인 후 리디렉션된 요청을 처리하고, Authorization Code를 이용해 토큰을 발급받습니다.
 *     tags: [OAuth]
 *     parameters:
 *       - in: query
 *         name: code
 *         schema:
 *           type: string
 *         required: true
 *         description: SmartThings에서 전달된 authorization code
 *       - in: query
 *         name: state
 *         schema:
 *           type: string
 *         required: false
 *         description: 인증 요청 시 전달한 상태값 (선택)
 *     responses:
 *       302:
 *         description: 인증 성공 후 앱으로 리다이렉션됩니다 (intent:// 또는 custom scheme 등)
 *       400:
 *         description: 필수 파라미터 누락 또는 유효하지 않은 요청
 *       500:
 *         description: 토큰 교환 실패 또는 서버 오류
 */
router.get('/callback', oauthCallback);

module.exports = router;
