export const mutations = {
  updateAccept: (state, payload) => {
    state.auth.isAccept = payload;
  },
  updateEmail: (state, payload) => {
    state.auth.email = payload;
  },
  updatePw: (state, payload) => {
    state.auth.pw = payload;
  },
  updatePwCheck: (state, payload) => {
    state.auth.pwcheck = payload;
  },
  updateverify: (state, payload) => {
    state.auth.verify[payload.index] = payload.data;
  },
};

export default mutations;
