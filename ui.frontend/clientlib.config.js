const path = require('path');

const BUILD_DIR = path.join(__dirname, 'dist');
const CLIENTLIB_DIR = path.join(
  __dirname,
  '..',
  'ui.apps',
  'src',
  'main',
  'content',
  'jcr_root',
  'apps',
  'practice',
  'clientlibs'
);

const libsBaseConfig = {
  allowProxy: true,
  serializationFormat: 'xml',
  cssProcessor: ['default:none', 'min:none'],
  jsProcessor: ['default:none', 'min:none']
};

module.exports = {
  context: BUILD_DIR,
  clientLibRoot: CLIENTLIB_DIR,
  libs: [
    {
      ...libsBaseConfig,
      name: 'clientlib-dependencies',
      categories: ['practice.dependencies'],
      assets: {
        js: {
          cwd: 'clientlib-dependencies',
          files: ['/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-dependencies',
          files: ['/*.css'],
          flatten: false
        }
      }
    },
    {
      ...libsBaseConfig,
      name: 'clientlib-base',  // Changed from clientlib-site to clientlib-base
      categories: ['practice.base'],  // Changed from practice.site to practice.base
      dependencies: ['practice.dependencies'],
      assets: {
        js: {
          cwd: 'clientlib-site',
          files: ['/*.js'],
          flatten: false
        },
        css: {
          cwd: 'clientlib-site',
          files: [
            '/*.css',
            '/styles/*.css'  // Add this if your styles are in a subdirectory
          ],
          flatten: false
        },
        resources: {
          cwd: 'clientlib-site',
          files: ['/*.*'],
          flatten: false,
          ignore: ['/*.js', '**/*.css']
        }
      }
    }
  ]
};
