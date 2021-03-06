import axios from 'axios';
import CONSTANT from './constant';

export default {
  updateDocument(uri, payload) {
    const {
      token,
      introduce,
      studyPlan,
    } = payload;

    return axios({
      url: CONSTANT.BASE_URI + uri,
      method: 'put',
      headers: { Authorization: `JWT ${token}` },
      data: {
        introduce,
        studyPlan,
      },
    });
  },

  getDocument(uri, token) {
    return axios.get(CONSTANT.BASE_URI + uri, {
      headers: { Authorization: `JWT ${token}` },
    });
  },

  updateGrades(uri, payload) {
    const {
      token,
      sendData,
    } = payload;

    return axios({
      url: CONSTANT.BASE_URI + uri,
      method: 'put',
      headers: { Authorization: `JWT ${token}` },
      data: sendData,
    });
  },

  getGrades(uri, token) {
    return axios.get(CONSTANT.BASE_URI + uri, {
      headers: { Authorization: `JWT ${token}` },
    });
  },

  getClassify(uri, token) {
    return axios.get(CONSTANT.BASE_URI + uri,
      { headers: { Authorization: `JWT ${token}` } },
    );
  },

  getInfo(uri, token) {
    return axios.get(CONSTANT.BASE_URI + uri,
      { headers: { Authorization: `JWT ${token}` } },
    );
  },

  getMypage(uri, token) {
    return axios.get(CONSTANT.BASE_URI + uri,
      { headers: { Authorization: `JWT ${token}` } },
    );
  },

  confirmSignup(code) {
    return axios.post(`${CONSTANT.CONFIRM_URI}${code}`);
  },

  getSchool(payload) {
    const {
      token,
      keyword,
      office,
    } = payload;

    return axios.get(`${CONSTANT.SCHOOL_URI}/search?name=${keyword}&government=${office}`, {
      headers: { Authorization: `JWT ${token}` },
    });
  },
};
