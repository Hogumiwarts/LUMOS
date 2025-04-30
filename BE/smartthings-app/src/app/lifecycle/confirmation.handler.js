const axios = require('axios');

exports.handleConfirmation = async (req, res) => {
  const confirmationUrl = req.body.confirmationData.confirmationUrl;
  try {
    await axios.get(confirmationUrl);
    console.log('✅ Confirmation successful!');
    res.status(200).send('Confirmation handled');
  } catch (error) {
    console.error('❌ Confirmation failed', error);
    res.status(500).send('Confirmation failed');
  }
};
