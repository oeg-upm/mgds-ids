<template>
    <div>
        <table class="table">
            <thead>
                <tr>
                    <th scope="col">ID</th>
                    <th scope="col">Resource</th>
                    <th scope="col">Erstellt</th>
                    <th scope="col">Status</th>
                </tr>
            </thead>
            <tbody>
                    <tr v-for="job in jobs" v-bind:key="job.id">
                        <th scope="row">{{job.id}}</th>
                        <td>{{job.resourceid}}</td>
                        <td>{{new Date(Date.parse(job.created_at)).toLocaleString()}}</td>
                        <td>
                            <!--CREATED-->
                            <span v-if="job.status == 0">
                                <span class="badge badge-primary">Erstellt</span>
                            </span>
                            <!--FINISHED-->
                            <span v-if="job.status == 2">
                                <span class="badge badge-success">Fertig</span>
                            </span>
                            <!--ERROR-->
                            <span v-if="job.status == 3">
                                <span class="badge badge-danger">Fehler</span>
                            </span>
                            <!--RUNNING-->
                            <span v-if="job.status == 1">
                                <span class="badge badge-info">Wird ausgeführt</span>
                            </span>
                        </td>
                    </tr>
            </tbody>
        </table>
        <button v-on:click="deleteAction" class="btn btn-info float-right">Jobs aufräumen</button>
    </div>
</template>

    <script>
            export default {
                name: "Jobs",
                data() {
                    return {
                    jobs: null
                    };
                },
                beforeMount (){
                    this.findAll()
                },
                methods:{
                    findAll () {
                        this.$axios({
                            method: 'get',
                            url: new URL('/api/jobs/find/all', this.$env.apiBaseUrl),
                            headers: {
                                Authorization: 'Bearer ' + localStorage.getItem('jwt')
                            }
                        })
                        .then(response => {
                                this.jobs = response.data;
                            })
                        .catch(error => {
                            if(error.response.status === 401){
                                this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                                this.$router.push("/login")                            
                            }
                        })
                    },
                    deleteAction (){
                        this.$axios({
                            method: 'get',
                            url: new URL('/api/jobs/delete/all', this.$env.apiBaseUrl),
                            headers: {
                                Authorization: 'Bearer ' + localStorage.getItem('jwt')
                            }
                        })
                        .then(response => {
                            this.$store.dispatch('update',response.data)
                            this.findAll()
                            })
                        .catch(error => {
                            if(error.response.status === 401){
                                this.$store.dispatch('update',{'status':'error','text':'Session expired.'})
                                this.$router.push("/login")                            
                            }
                        })
                    }
                }
            }
        </script>
