<template>
    <div>
        <div class="card" style="margin-bottom: 20px;">
            <div class="card-body" style="display: flex;justify-content: center;">
                <p class="card-text">
                    Registrierte Datensätze
                </p>
            </div>
        </div>
        <v-app class="vuetifyApp">
            <v-container style="display: flex; align-items: center ;padding: initial;">
                <v-text-field
                        v-model="search"
                        append-icon="search"
                        label="Search"
                        single-line
                        style="margin-right: 30px;"
                >
                </v-text-field>
                <v-select
                        attach
                        label="Datasource Type"
                        :items="getAllDatasourceTypes"
                        class="selectAdapter"
                        v-model="datasourceType"
                        clearable></v-select>
                <v-select
                        attach
                        label="Datasource Name"
                        :items="getAllDatasourceNames"
                        class="selectAdapter"
                        v-model="datasourceName"
                        clearable></v-select>
                <v-switch :disabled="disablePublish" label="Published"  class="switch-label"
                          v-model="publish"></v-switch>
                <v-switch :disabled="disableUnpublish" label="Unpublished" class="switch-label"
                          v-model="unpublish"></v-switch>
                <v-btn v-if="checkIfAllPublished===true" @click="publishAll" class="publishAll">Alle Veröffentlichen
                </v-btn>
                <v-btn v-if="checkIfAllPublished===false" @click="unPublishAll" class="unPublishAll">Zurückhalten
                </v-btn>
                <v-btn v-if="filteredItems.length==0" :disabled="true" class="publishAllDisabled">Alle Veröffentlichen
                </v-btn>
            </v-container>

            <v-data-table :items="filteredItems"
                          @current-items="getCurrentItems"
                          :headers="headers"
                          hide-default-footer
                          :page.sync="page"
                          :items-per-page="itemsPerPage"
                          :search="search"
                          :custom-filter="filter"
                          multi-sort
                          @page-count="pageCount = $event"
                          class="table zui-table-highlight">
                <template v-slot:body="{ items }">
                    <tbody class="tableBody" >
                    <tr v-if="items.length==0">
                        <td colspan="100%" style="text-align: center ;font-size: 20px;font-family: monospace;">Es sind noch keine Datensätze registriert.</td>
                    </tr>
                    <tr v-else v-for="item in items" >
                        <td @click="rowClick(item)">
                            <p :id="'truncateTitle-'+item.id" class="truncateText" style="-webkit-line-clamp: 2;">
                                {{item.datasettitle}}
                            </p>
                        </td>
                        <td @click="rowClick(item)"><p :id="'truncateNote-'+item.id" class="truncateText" style="-webkit-line-clamp: 2;">
                            {{item.datasetnotes}}
                        </p></td>
                        <td><p>
                            {{getDatasource(item.sourceid).datasourcetype}}
                        </p></td>
                        <td><p>
                            {{getDatasource(item.sourceid).datasourcename}}
                        </p></td>
                        <td>
                            <div style="display: flex">
                               <span v-if="item.status == 1">
                                  <button v-on:click="publishAction(item.id)">
                                      <span title="Veröffentlichen" style="color: #5d5d5d" aria-hidden="true"
                                            class="v-icon  material-icons ">publish</span></button>
                            </span>
                                <span v-if="item.status == 2">
                                 <button v-on:click="unpublishAction(item.id)">
                                 <span title="Zurückhalten" style="color: #00bc8c;text-shadow: 0 0 5px #919191;"
                                       aria-hidden="true" class="v-icon  material-icons ">publish</span></button>
                            </span>

                                <button v-on:click="deleteAction(item.id)">
                            <span title="Delete"
                                  style="    color: rgb(120, 2, 0);text-shadow: 0 0 2px white;     margin-right: 3px;"
                                  aria-hidden="true"
                                  class="v-icon material-icons t">delete</span>
                                </button>

                                <a :href="'http://localhost:8090/data/'+item.id">
                                <span title="Download"
                                      style="color: #3498DB;text-shadow: 0 0 2px white;"
                                      aria-hidden="true"
                                      class="v-icon material-icons t">system_update_alt</span></a>
                            </div>
                        </td>
                    </tr>
                    </tbody>
                </template>
            </v-data-table>
            <div class="text-center pt-2">
                <v-pagination v-model="page" :length="pageCount"></v-pagination>
                <v-text-field
                        :value="itemsPerPage"
                        label="Items per page"
                        type="number"
                        min="-1"
                        max="15"
                        @input="itemsPerPage = parseInt($event, 10)"
                ></v-text-field>
            </div>
        </v-app>

        <!--
        <span v-for="dataAsset in dataassets" v-bind:key="dataAsset.id">
                    <div class="card">
                        <div class="card-header">
                            <h4>{{dataAsset.datasettitle}}</h4>
                        </div>
                        <div class="card-body">
                            <h5 class="card-title"></h5>
                            <p class="card-text">
                                <h5>Beschreibung</h5>
                                <p>
                                    {{dataAsset.datasetnotes}}
                                </p>
                                <h5>Resourcen</h5>
                                <p>
                                <a :href="'http://localhost:8090/data/'+dataAsset.id" class=" badge badge-info">Download Rohdaten</a>
                                -->
        <!--  <a href="@urlService.getOriginalResourceURL(dataAsset)" target="_blank" class="badge badge-info">Zur Quelle</a>-->
        <!--           </p>
                   <h5>IDS Download</h5>
                    <p>
                   <a :href="'http://localhost:8090/data/'+dataAsset.id" class="badge badge-success">IDS</a>
                   <a :href="'http://localhost:8090/data/'+dataAsset.id+'.txt'" class="badge badge-primary">IDS+TXT</a>
                   <a :href="'http://localhost:8090/data/'+dataAsset.id+'.json'" class="badge badge-primary">IDS+JSON</a>
                   </p>
           </div>
           <div class="card-footer text-muted">
               <span v-if="dataAsset.status == 1">
                     <button v-on:click="publishAction(dataAsset.id)" class="btn btn-success">Veröffentlichen</button>
               </span>
               <span v-if="dataAsset.status == 2">
                    <button v-on:click="unpublishAction(dataAsset.id)" class="btn btn-primary">Zurückhalten</button>
               </span>
                <button v-on:click="deleteAction(dataAsset.id)" class="btn btn-danger">Löschen</button>
           </div>
       </div>
   <br />
</span>-->
    </div>
</template>
<script>
    export default {
        name: "DataAssets",
        data() {
            return {
                dataassets: [],
                publish: false,
                unpublish: false,
                disablePublish: false,
                disableUnpublish: false,
                page: 1,
                pageCount: 0,
                itemsPerPage: 5,
                search: '',
                datasourcetypes: [],
                currentItems: [],
                datasourceType: "",
                datasourceName: "",
                headers: [
                    {
                        text: 'Title',
                        dataType:'button',
                        value: 'datasettitle',
                        align: 'center',
                        sortable: false,
                        class: "text-datasource"
                    },
                    {text: 'Beschreibung', align: 'center', value: 'datasetnotes'},
                    {text: 'Datasource Type', align: 'center', value: 'datasourcetype', class: "text-datasource"},
                    {text: 'Datasource Name', align: 'center', value: 'datasourcename', class: "text-datasource"},
                    {text: 'Actions', align: 'center', value: 'status', sortable: false, class: "text-action"}
                ]
            };
        },
        beforeMount() {
            this.setData()
        },
        created() {
        this.$store.dispatch('loadSources', {apiBase:this.$env.apiBaseUrl, configBase:this.$env.configManagerUrl});
        },
        computed: {
            sources() {
                return this.$store.state.sources;
            },
            checkIfAllPublished() {
                let statusArr = []
                for (let i in this.filteredItems) {
                    let id = this.filteredItems[i].status
                    statusArr.push(id)
                }
                if (statusArr.includes(2, 0)) {
                    if (statusArr.includes(1, 0)) {
                        return true
                    } else {
                        this.disableUnpublish = true
                        return false
                    }
                }
                if (statusArr.includes(1, 0)) {
                    if (!statusArr.includes(2, 0)) {
                        this.disablePublish = true
                        return true
                    }
                    return true
                }
            },
            mungedDatasets() {
                return this.dataassets.map((v) => {
                    return {
                        ...v,
                        datasourcetype: this.getDatasource(v.sourceid).datasourcetype,
                        datasourcename: this.getDatasource(v.sourceid).datasourcename
                    }
                })
            },
            filteredItems() {
                let filteredItemsByAdapterNames = this.mungedDatasets.filter((row) => {
                    return !this.datasourceName || (row.datasourcename === this.datasourceName);
                })
                let filteredItemsByAdapterTypes = filteredItemsByAdapterNames.filter((row) => {
                    return !this.datasourceType || (row.datasourcetype === this.datasourceType);
                })
                if (this.mungedDatasets.length == 0) {
                    this.publish =false
                    this.unpublish = false
                    this.disableUnpublish = true
                    this.disablePublish= true
                }
                else {
                    if (this.publish) {
                        this.unpublish = false;
                        this.disableUnpublish = true
                        let filteredItemsByAdapterStatus = this.mungedDatasets.filter((row) => {
                            return (row.status === 2);
                        })
                        let filteredItemsByAdapterNames2 = filteredItemsByAdapterStatus.filter((row) => {
                            return !this.datasourceName || (row.datasourcename === this.datasourceName);
                        })
                        let  filteredItemsByAdapterTypes2 = filteredItemsByAdapterNames2.filter((row) => {
                            return !this.datasourceType || (row.datasourcetype === this.datasourceType);
                        })
                        return filteredItemsByAdapterTypes2
                    } else {
                        this.disableUnpublish = false
                    }
                    if (this.unpublish) {
                        this.publish = false;
                        this.disablePublish = true
                        let filteredItemsByAdapterStatus = this.mungedDatasets.filter((row) => {
                            return (row.status === 1);
                        })
                        let filteredItemsByAdapterNames2 = filteredItemsByAdapterStatus.filter((row) => {
                            return !this.datasourceName || (row.datasourcename === this.datasourceName);
                        })
                        let  filteredItemsByAdapterTypes2 = filteredItemsByAdapterNames2.filter((row) => {
                            return !this.datasourceType || (row.datasourcetype === this.datasourceType);
                        })
                        return filteredItemsByAdapterTypes2
                    } else {
                        this.disablePublish = false
                    }
                }

                return filteredItemsByAdapterTypes
            },
            getAllDatasourceTypes() {
                let datasource = []
                for (let i in this.dataassets) {
                    let id = this.dataassets[i].sourceid
                    datasource.push(this.getDatasource(id).datasourcetype)
                }
                return datasource
            },
            getAllDatasourceNames() {
                let datasource = []
                for (let i in this.dataassets) {
                    let id = this.dataassets[i].sourceid
                    datasource.push(this.getDatasource(id).datasourcename)
                }
                return datasource
            },
        },
        methods: {
            getCurrentItems(e) {
                this.currentItems = e
            },
            rowClick(item) {
                let collapseNote = document.getElementById("truncateNote-" + item.id).style.getPropertyValue("-webkit-line-clamp")
                let collapseTitle = document.getElementById("truncateTitle-" + item.id).style.getPropertyValue("-webkit-line-clamp")
                if (collapseTitle == 2 || collapseNote == 2) {
                    document.getElementById("truncateNote-" + item.id).style = "-webkit-line-clamp:none; "
                    document.getElementById("truncateTitle-" + item.id).style = "-webkit-line-clamp:none;"
                } else {
                    document.getElementById("truncateNote-" + item.id).style = "-webkit-line-clamp:2; "
                    document.getElementById("truncateTitle-" + item.id).style = "-webkit-line-clamp:2;"
                }
            },
            filter(value, search, item) {
                let adapterDatasetnotes = RegExp(search, 'i').test(item.datasetnotes)
                let adapterDatasettitle = RegExp(search, 'i').test(item.datasettitle)
                let adapterStatus = RegExp(search, 'i').test(item.status)
                let adapterType = RegExp(search, 'i').test(this.getDatasource(item.sourceid).datasourcetype)
                let adapterName = RegExp(search, 'i').test(this.getDatasource(item.sourceid).datasourcename)

                return adapterType || adapterName
                    || adapterDatasetnotes || adapterDatasettitle
                    || adapterStatus
            },
            setData() {
                this.$axios({
                    method: 'get',
                    url: new URL('/api/dataassets', this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                    .then(response => {
                        this.dataassets = response.data;
                    })
                    .catch(error => {
                        if (error.response.status === 401) {
                            this.$store.dispatch('update', {'status': 'error', 'text': 'Session expired.'})
                            this.$router.push("/login")
                        }
                    })
            },
            getDatasource(datasetid) {
                let datasource = []
                for (let i in this.sources) {
                    let result = this.sources[i].sources
                    for (let i2 in result) {
                        let result2 = this.sources[i].sources[i2].id
                        if (datasetid == result2) {
                            datasource = this.sources[i].sources[i2]
                        }
                    }
                }
                return datasource
            },
            publishAll() {
                this.$axios({
                    method: 'get',
                    url: new URL('/api/dataassets/all/publish', this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                    .then(response => {
                        for (let i in this.filteredItems) {
                            let id = this.filteredItems[i].id
                            this.dataassets.find(da => da.id == id).status = 2
                        }
                    })
                    .catch(error => {
                        if (error.response.status === 401) {
                            this.$store.dispatch('update', {'status': 'error', 'text': 'Session expired.'})
                            this.$router.push("/login")
                        }
                    })
                this.unpublish = false
            },
            unPublishAll() {
                this.$axios({
                    method: 'get',
                    url: new URL('/api/dataassets/all/unpublish', this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                .then(response => {
                    for (let i in this.filteredItems) {
                        let id = this.filteredItems[i].id
                        this.dataassets.find(da => da.id == id).status = 1
                    }
                })
                .catch(error => {
                    if (error.response.status === 401) {
                        this.$store.dispatch('update', {'status': 'error', 'text': 'Session expired.'})
                        this.$router.push("/login")
                    }
                })
                this.publish = false
            },

            publishAction(id) {
                this.$axios({
                    method: 'get',
                    url: new URL('/api/dataassets/' + id + '/publish', this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                    .then(response => {
                        this.dataassets.find(da => da.id == id).status = 2
                    })
                    .catch(error => {
                        if (error.response.status === 401) {
                            this.$store.dispatch('update', {'status': 'error', 'text': 'Session expired.'})
                            this.$router.push("/login")
                        }
                    })
            },
            unpublishAction(id) {
                this.$axios({
                    method: 'get',
                    url: new URL('/api/dataassets/' + id + '/unpublish', this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                    .then(response => {
                        this.dataassets.find(da => da.id == id).status = 1
                    })
                    .catch(error => {
                        if (error.response.status === 401) {
                            this.$store.dispatch('update', {'status': 'error', 'text': 'Session expired.'})
                            this.$router.push("/login")
                        }
                    })
            },
            deleteAction(id) {
                this.$axios({
                    method: 'get',
                    url: new URL('/api/dataassets/' + id + '/delete', this.$env.apiBaseUrl),
                    headers: {
                        Authorization: 'Bearer ' + localStorage.getItem('jwt')
                    }
                })
                    .then(response => {
                        this.$store.dispatch('update', response.data)
                        this.setData()
                    })
                    .catch(error => {
                        if (error.response.status === 401) {
                            this.$store.dispatch('update', {'status': 'error', 'text': 'Session expired.'})
                            this.$router.push("/login")
                        }
                    })
            }
        }
    }
</script>
<style>
    .switch-label {
        margin-right: 30px;
    }

    .switch-label .theme--light.v-label {
        margin-bottom: initial;
    }

    .switch-label .v-input--switch__track {
        color: rgba(255, 255, 255, 0.79) !important
    }

    .v-application .switch-label .primary--text {
        color: #3498DB !important;
    }

    .theme--light.v-label--is-disabled {
        color: #4d4d4d !important
    }

    .v-input--is-label-active label {
        color: #3498DB !important;
    }

    .theme--light.v-input--switch.v-input--is-disabled:not(.v-input--is-dirty) .v-input--switch__track {
        color: #4d4d4d !important
    }

    .theme--light.v-input--switch.v-input--is-disabled:not(.v-input--is-dirty) .v-input--switch__thumb {
        color: #4d4d4d !important
    }

    .text-datasource {
        width: 150px;
    }

    .text-action {
        width: 100px;
    }

    .publishAll {
        color: #fff !important;
        background-color: #00bc8c !important;
        border-color: #00bc8c;
        font-weight: 400;
        text-align: center;
        white-space: nowrap;
        vertical-align: middle;
        text-transform: none;
        width: 160px;
    }

    .unPublishAll {
        color: #fff !important;
        background-color: #375a7f !important;
        border-color: #375a7f;
        text-transform: none;
        width: 160px;
    }
    .publishAllDisabled {
        color: #4d4d4d !important;
        background-color: #4d4d4d !important;
        border-color: #375a7f;
        text-transform: none;
        width: 160px;
    }
    .publishAllDisabled .v-btn__content{
        color: #000000;

    }
    .vuetifyApp.theme--light.v-application {
        background: #222;
    }

    .table {
        border-radius: 0.25rem;
        background-color: #444;
    }

    .v-application .primary--text {
        color: white !important;
    }

    .v-application a {
        color: white;
    }

    .v-data-table-header {
        background-color: #444;
        color: white;
        border-bottom: 1px solid rgba(0, 0, 0, 0.125);
        padding: 0.75rem 1.25rem;
        border-top-left-radius: 0.25rem;
        border-top-right-radius: 0.25rem;
    }

    .v-data-footer {
        border-bottom-left-radius: 0.25rem;
        border-bottom-right-radius: 0.25rem;
        background-color: #444;
        color: white;
    }

    .theme--light.v-input input {
        color: white;
    }

    .v-text-field--single-line {
        color: white !important;
    }

    .tableBody {
        background-color: #303030;
        color: white;
        padding: 1.25rem;
    }

    .table thead th {
        border: 1px solid rgba(0, 0, 0, 0.125);
    }

    .table tbody td {
        border: 1px solid rgba(0, 0, 0, 0.125);
        padding: 10px;
    }

    .zui-table-highlight tbody tr:hover {
        background-color: rgba(4, 1, 3, 0.22) !important;
    }

    .theme--light.v-data-table {
        background: #222;
    }

    .v-select__selection--comma {
        color: white !important;
        padding-left: 3px;
    }

    .theme--light.v-icon {
        color: black;
    }

    .v-btn--round .v-btn__content .v-icon {
        color: white;
    }

    .theme--light.v-label {
        color: white;
    }

    .truncateText {
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        /* number of lines to show */
        -webkit-box-orient: vertical;
    }

    .selectAdapter {
        margin-right: 30px;
    }

    .selectAdapter .theme--light.v-list {
        background: #303030;
    }

    .selectAdapter .theme--light.v-list-item:not(.v-list-item--active):not(.v-list-item--disabled) {
        color: rgba(255, 255, 255, 0.87) !important;
    }

    .table.theme--light.v-data-table thead tr th {
        color: white
    }
</style>