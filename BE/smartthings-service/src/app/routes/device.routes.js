const express = require('express');
const router = express.Router();
const {
  getDevices,
  getDeviceStatus,
  executeDeviceCommand,
} = require('../controllers/device.controller');

/**
 * @swagger
 * tags:
 *   - name: Device
 *     description: SmartThings 디바이스 제어 API
 */

/**
 * @swagger
 * /smart/devices:
 *   get:
 *     summary: 디바이스 목록 조회
 *     tags: [Device]
 *     parameters:
 *       - in: header
 *         name: installedappid
 *         required: true
 *         schema:
 *           type: string
 *         description: SmartApp 설치 ID
 *     responses:
 *       200:
 *         description: 디바이스 목록 반환
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                 devices:
 *                   type: array
 *                   items:
 *                     type: object
 */
router.get('/', getDevices);

/**
 * @swagger
 * /smart/devices/{deviceId}/status:
 *   get:
 *     summary: 특정 디바이스 상태 조회
 *     tags: [Device]
 *     parameters:
 *       - in: path
 *         name: deviceId
 *         required: true
 *         schema:
 *           type: string
 *         description: 조회할 디바이스 ID
 *       - in: header
 *         name: installedappid
 *         required: true
 *         schema:
 *           type: string
 *         description: SmartApp 설치 ID
 *     responses:
 *       200:
 *         description: 디바이스 상태 반환
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 *                 status:
 *                   type: object
 */
router.get('/:deviceId/status', getDeviceStatus);

/**
 * @swagger
 * /smart/devices/{deviceId}/command:
 *   post:
 *     summary: 특정 디바이스 명령 실행
 *     tags: [Device]
 *     parameters:
 *       - in: path
 *         name: deviceId
 *         required: true
 *         schema:
 *           type: string
 *         description: 제어할 디바이스 ID
 *       - in: header
 *         name: installedappid
 *         required: true
 *         schema:
 *           type: string
 *         description: SmartApp 설치 ID
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               commands:
 *                 type: array
 *                 description: 실행할 명령 목록
 *                 items:
 *                   type: object
 *                   properties:
 *                     component:
 *                       type: string
 *                     capability:
 *                       type: string
 *                     command:
 *                       type: string
 *                     arguments:
 *                       type: array
 *                       items:
 *                         type: string
*           example:
 *             commands:
 *               - component: main
 *                 capability: switch
 *                 command: on
 *                 arguments: []
 *               - component: main
 *                 capability: airConditionerFanMode
 *                 command: setFanMode
 *                 arguments: [high]
 *     responses:
 *       200:
 *         description: 명령 실행 성공
 *         content:
 *           application/json:
 *             schema:
 *               type: object
 *               properties:
 *                 success:
 *                   type: boolean
 */
router.post('/:deviceId/command', executeDeviceCommand);

module.exports = router;
