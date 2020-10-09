<template>
    <div>
        <div class="text-center">
            <p>
                <img src="../../assets/images/ids-white.png" alt="Logo of Industrial data space" class="navbar-logo mx-auto" style="width: auto; height: 50px;">
            </p>
        </div>
        <div class="card mx-auto" style="width: 30rem;">
            <div class="card-body">
                <form @submit.prevent="login">
                    <div class="form-group">
                        <label for="username">Nutzername</label>
                        <input type="text"
                        class="form-control"
                        id="username"
                        required v-model="username">
                        <label for="password">Passwort</label>
                        <input type="password"
                        class="form-control"
                        id="password"
                        required v-model="password">
                    </div>
                    <button v-on:click="login()" class="btn btn-primary">Login</button>
                </form>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name:"Login",
    data() {
        return {
            id: null,
            username:null,
            password:null
        };
    },
    beforeDestroy(){
        this.$store.state.info = null;
    },
    beforeRouteEnter(to, from, next){
        if(localStorage.getItem('jwt') !== null){
            localStorage.removeItem('jwt')
        }
        next()
    },
    methods:{
        login() {
           this.$axios({
                method: 'post',
                url: new URL('/login', this.$env.apiBaseUrl),
                data: {
                    username: this.username,
                    password: this.password
                    }
                })
                .then( response => {
                    if(response.status == 200) {
                        localStorage.setItem('jwt',response.data)
                        this.$router.push("/")
                    }
                }
                )
        }
    }
}
</script>