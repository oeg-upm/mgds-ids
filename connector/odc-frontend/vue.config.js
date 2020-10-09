const buildMode = process.env.NODE_ENV === 'production' && process.env.BUILD_MODE ?
  process.env.BUILD_MODE :
  'dev';

// eslint-disable-next-line no-console
console.log(`You are using the vue config on build mode '${buildMode}'.`)

module.exports = {
  publicPath: buildMode === 'build' ?
    '/browse/' :
    '/',
  runtimeCompiler: true,
  chainWebpack: (config) => {
    config.plugins.delete('prefetch')
  },
  pluginOptions: {
    webpackBundleAnalyzer: {
      analyzerMode: 'disable',
      openAnalyzer: false
    }
  }
};
