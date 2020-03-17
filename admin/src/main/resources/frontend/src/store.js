import axios from "axios"
import Vue from "vue"
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        allModels: [],
        ready: false,
        loggedInUser: null,
        token: null,
        inviteLink: null,
        hasUsers: null,
        settings: null
    },
    mutations: {
        SET_MODELS(state, models) {
            state.allModels = models;
        },
        SET_USER(state, user) {
            state.loggedInUser = user
        },
        SET_TOKEN(state, token) {
            state.token = token;
        },
        SET_HAS_USERS(state, value) {
            state.hasUsers = value
        },
        READY(state) {
            state.ready = true;
        }

    },
    actions: {

        async getModels({commit}) {
            return axios.get("/config")
                .then(response => commit('SET_MODELS', response.data.filter(m => m.viewableOnCMS)));
        },

        async init({dispatch, commit}) {
            let token = window.localStorage.getItem('token');

            await axios.get("/elepy-has-users")
                .then(() =>
                    commit('SET_HAS_USERS', true))
                .catch(() =>
                    commit('SET_HAS_USERS', false));
            if (token != null) {
               return dispatch('logInWithToken', token)
                    .catch(() => window.localStorage.removeItem('token'))
                    .finally(() => commit("READY"));
            } else {
               return commit("READY");
            }
        },
        async logInWithToken({commit, dispatch}, loginResponseToken) {
            let userResponse = (await axios({
                url: "/elepy-logged-in-user",
                method: 'get',
                headers: {'Authorization': 'Bearer ' + loginResponseToken}
            })).data;

            await dispatch('getModels');

            window.localStorage.setItem("token", loginResponseToken);

            axios.defaults.headers.authorization = 'Bearer ' + loginResponseToken;
            
            commit("SET_USER", userResponse);
            commit("SET_TOKEN", loginResponseToken)
        },
        async logIn({dispatch}, loginAttempt) {
            delete axios.defaults.headers["authorization"];
            let loginResponseToken = (await axios({
                url: "/elepy-token-login",
                method: 'post',
                auth: {
                    username: loginAttempt.username,
                    password: loginAttempt.password
                }
            })).data;
            return dispatch('logInWithToken', loginResponseToken)
        },

        logOut({commit}) {
            document.cookie = "ELEPY_TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
            window.localStorage.removeItem('token');
            delete axios.defaults.headers["authorization"];
            commit("SET_USER", null);
        }
    },
    getters: {
        getModel: state => (modelPath) => state.allModels.filter((m) => m.path.includes(modelPath))[0],

        isModerator: (state, getters) =>
            getters.loggedIn && (
            state.loggedInUser.permissions.includes("moderator")
            || state.loggedInUser.permissions.includes("owner")),

        loggedIn: state => state.loggedInUser != null,

        elepyInitialized: state => {
            return state.hasUsers
        },

        ready: state => state.ready === true

    }
});