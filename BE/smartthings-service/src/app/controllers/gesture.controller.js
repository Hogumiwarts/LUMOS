const smartApp = require('../../config/smartapp');

exports.getGesture = async (req, res) => {

    const memberId = req.params.memberId;

    if (!memberId) {
        return res.status(400).json({ error: 'Member ID는 필수입니다.' });
    }

}