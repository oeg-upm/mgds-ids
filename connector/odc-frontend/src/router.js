import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'

Vue.use(Router)

const ifNotAuthenticated = (to, from, next) => {
  if (localStorage.getItem('jwt') !== null) {
    next()
    return
  }
  next('/login')
}

const ifAuthenticated = (to, from, next) => {
  if (localStorage.getItem('jwt') == null) {
    next()
    return
  }
  next('/')
}

export default new Router({

  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('./views/Auth/Login.vue'),
      ifAuthenticated
    },
    {
      path: '/about',
      name: 'connector',
      component: () => import('./views/Connector.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/about.:ext',
      name: 'connector',
      component: () => import('./views/Connector.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/job',
      name: 'jobs',
      component: () => import('./views/Job/Jobs.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/dataasset/',
      name: 'dataassets',
      component: () => import('./views/DataAsset/DisplayDataAssets.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/dataasset/create/:sourceid',
      name: 'createdataasset',
      component: () => import('./views/DataAsset/CreateDataAsset.vue'),
      props: true,
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/datasource/select',
      name: 'selectdatasource',
      component: () => import('./views/DataAsset/SelectDataSource.vue'),
      props: true,
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/datasource/create',
      //name: 'createdatasource',
      component: () => import('./views/DataAsset/CreateDataSource.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/datasource/create/:typeAdapter',
      //name: 'createdatasource',
      component: () => import('./views/DataAsset/CreateDataSource.vue'),
      props:true,
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/datasource/edit/:typeAdapter/:sourceid',
      name: 'editdatasource',
      component: () => import('./views/DataAsset/CreateDataSource.vue'),
      props: true,
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/apps',
      name: 'apps',
      component: () => import('./views/Apps/AppMenu.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/brokers',
      name: 'brokers',
      component: () => import('./views/Brokers/Brokers.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/configuration',
      name: 'configuration',
      component: () => import('./views/Configuration/Configuration.vue'),
      beforeEnter: ifNotAuthenticated
    },
    {
      path: '/fileupload',
      name: 'fileupload',
      component: () => import('./views/DataAsset/FileUpload.vue'),
      beforeEnter: ifNotAuthenticated
    },
  ]
})
