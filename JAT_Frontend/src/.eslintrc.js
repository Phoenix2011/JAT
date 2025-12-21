module.exports = {
  root: true,
  env: {
    node: true
  },
  extends: [
    'plugin:vue/vue3-essential',
    'eslint:recommended'
  ],
  parserOptions: {
    parser: 'babel-eslint'
  },
  rules: {
    'no-console': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'warn' : 'off',
    'no-unused-vars':'off'//意思就是不让eslint校验我定义有没有用了。
  }
  // parserOptions: {
  //     sourceType: 'module'
  // },
  // parser: "vue-eslint-parser",
  // env: {
  //   browser: true,
  //   node: true,
  //   es6: true,
  // },
  // rules: {
  //   'no-console': 'off',
  //   'no-unused-vars':'off'//意思就是不让eslint校验我定义有没有用了。
  // }
}