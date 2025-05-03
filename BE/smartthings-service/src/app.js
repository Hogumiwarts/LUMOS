const express = require('express');
require('dotenv').config({ path: '../.env' });

const swaggerUi = require('swagger-ui-express');
const swaggerSpec = require('./config/swagger');

const app = express();

const smartRoutes = require('./app/routes/smart.routes');
const authRoutes = require('./app/routes/auth.routes');
const deviceRoutes = require('./app/routes/device.routes');

app.use("/smart/v3/api-docs", swaggerUi.serve, swaggerUi.setup(swaggerSpec));

app.use(express.json());

// /smart 엔드포인트 등록
app.use('/smart', smartRoutes);

// OAuth 관련 API
app.use('/smart/oauth', authRoutes);

// 디바이스 관련 API
app.use('/smart/devices', deviceRoutes);

const port = process.env.PORT || 3000;
app.listen(port, () => {
  console.log(`✅ Server running at http://localhost:${port}`);
  console.log('✅ Swagger docs at http://localhost:3000/smart/v3/api-docs/');
});
