const smartApp = require('../../config/smartapp');

exports.getDevices = async (req, res) => {
  const installedAppId = req.headers.installedappid;

  try {
    const ctx = await smartApp.withContext(installedAppId);
    const devices = await ctx.api.devices.list();
    res.send({ success: true, devices });
  } catch (error) {
    res.status(500).send({ success: false, message: error.message });
  }
};

exports.getDeviceStatus = async (req, res) => {
  const { deviceId } = req.params;
  const installedAppId = req.headers.installedappid;

  try {
    const ctx = await smartApp.withContext(installedAppId);
    const status = await ctx.api.devices.getStatus(deviceId);
    res.send({ success: true, status });
  } catch (error) {
    res.status(500).send({ success: false, message: error.message });
  }
};

exports.executeDeviceCommand = async (req, res) => {
  const { deviceId } = req.params;
  const installedAppId = req.headers.installedappid;
  const { commands } = req.body; // [{component, capability, command, arguments}]

  try {
    const ctx = await smartApp.withContext(installedAppId);
    await ctx.api.devices.executeCommands(deviceId, commands);
    res.send({ success: true });
  } catch (error) {
    res.status(500).send({ success: false, message: error.message });
  }
};

exports.getSupportedDeviceCommands = async (req, res) => {
  const { deviceId, componentId, capabilityId } = req.params;
  const installedAppId = req.headers.installedappid;

  try {
    const ctx = await smartApp.withContext(installedAppId);

    // optional: 디바이스가 해당 capability를 갖고 있는지 확인
    const capabilityInfo = await ctx.api.capabilities.get(capabilityId, '1');
    const commands = capabilityInfo.commands || [];

    res.send({ success: true, commands });
  } catch (error) {
    res.status(500).send({ success: false, message: error.message });
  }
};