<template>
  <div class="modal" v-if="index > 0">
    <div class="modal__background" @click="closeModal"></div>
    <div class="modal__contants" v-if="index < 7">
      <div class="modal__contants__close" v-if="index < 7" @click="closeModal"></div>
      <login v-if="index === 1"/>
      <reset-pw v-if="(index >= 2 && index < 6)"/>
      <email-verify v-if="index === 6"/>
    </div>
    <div class="modal__contants modal__contants--confirm" v-if="(index >= 7 && index<10)">
      <div class="modal__contants__close" @click="closeModal"></div>
      <verify v-if="index === 7"/>
      <finish v-if="index === 8"/>
      <check-modal v-if="index === 9"/>
    </div>
    <div class="modal__contants modal__contants--branch" v-if="index>9">
      <div class="modal__contants__close modal__contants__close--branch" @click="closeModal"></div>
      <branch-modal v-if="index===10"/>
    </div>
  </div>
</template>

<script>
// div class="modal__background" @click="changeModal"></div>  고치기
import Login from './Login';
import ResetPw from './ResetPw';
import CheckModal from './CheckModal';
import Verify from './Verify/Verify';
import Finish from './Verify/Finish';
import EmailVerify from './EmailVerify';
import branchModal from './branchModal';

export default {
  name: 'Modal',
  props: ['email'],
  components: {
    Login,
    ResetPw,
    CheckModal,
    Verify,
    Finish,
    EmailVerify,
    branchModal,
  },
  computed: {
    index() {
      return this.$store.state.modal.index;
    },
  },
  methods: {
    closeModal() {
      this.$store.commit('changeIndex', {
        index: 0,
      });
    },
  },
};
</script>

<style lang="scss">
@import '../../style/setting';
$M-input-background: #f9fbfc;
$M-input-placeholder: #acc6c9;
$M-input-color: #26484c;
$M-btn-background: #eff7f8;
$M-btn-color: #296169;
$M-modal-color: #5a8f97;
$M-title-color: #000;
$M-close-color: #dee8e9;

.modal{
  position: fixed;
  z-index: 99;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  @include e('background'){
    width: 100%;
    height: 100%;
    background-color: rgba($color: #8C8C8C, $alpha: 0.3);
  }
  @include e('contants'){
    position: absolute;
    left: calc(50% - 225px);
    top: calc(50% - 200px);
    width: 400px;
    height: 450px;
    background-color: #FFF;
    display: flex;
    justify-content: center;
    align-items: center;
    color: $M-modal-color;
    animation: modalpop 0.5s;
    @include m('confirm') {
      left: calc(50% - 350px);
      top: calc(50% - 300px);
      width: 700px;
      height: 600px;
      animation: confirmModalPop 0.5s;
    }
    @include m('branch') {
      left: calc(50% - 250px);
      top: calc(50% - 150px);
      width: 500px;
      height: 300px;
      animation: branchModalPop 0.5s;
    }
    @include e('close'){
        position: absolute;
        cursor: pointer;
        top: 25px;
        right: 20px;
        width: 18px;
        height: 18px;
        &::before, &::after{
          position: relative;
          content: "";
          display: block;
          z-index: 7;
          width: 18px;
          height: 2px;
          border-radius: 10px;
          background-color: $M-close-color;
        }
        &::before{
          transform: rotate(-45deg);
          top: 2px;
        }
        &::after{
          transform: rotate(45deg);
        }
        @include m('branch') {
          top: 10px;
          right: 5px;
        }
    }
  }
}

.modal--input{
    width: 231px;
    height: 14px;
    border-radius: 5px;
    background-color: $M-input-background;
    padding: 12px 0px 14px 19px;
    font-size: 14px;
    color: $M-input-color;
    border: 0;
    margin-top: 10px;
    &::placeholder{
      color: $M-input-placeholder;
      font-family: 'NanumSquareRound';
  }
}

.modal--btn{
  width: 250px;
  height: 40px;
  border-radius: 5px;
  background-color: $M-btn-background;
  border: 0;
  color: $M-btn-color;
  text-align: center;
  line-height: 40px;
  margin-top: 10px;
  cursor: pointer;
  font-size: 14px;
  transition: 0.5s;
  border: 0;
  &:hover{
    background-color: #d5edf0;
    transition: 0.5s;
  }
}
.error-msg{
  position: absolute;
  top: 30px;
  left: 46px;
  z-index: 6;
  display: block;
  animation: errormsg 4s normal;
  opacity: 0;
}

.modal-title{
  position: relative;
  color: $M-title-color;
  font-size: 24px;
  text-align: center;
  &::after{
    content: '';
    display: block;
    position: relative;
    top: 17px;
    margin: 0 auto;
    width: 100px;
    height: 1px;
    background-color: #70b1ba;
  }
}
.input-shake{
  animation: shake 0.82s cubic-bezier(.36,.07,.19,.97) both;
  transform: translate3d(0, 0, 0);
  backface-visibility: hidden;
  perspective: 1000px;
}
@keyframes shake {
  10%, 90% {
    transform: translate3d(-1px, 0, 0);
  }
  20%, 80% {
    transform: translate3d(2px, 0, 0);
  }
  30%, 50%, 70% {
    transform: translate3d(-4px, 0, 0);
  }
  40%, 60% {
    transform: translate3d(4px, 0, 0);
  }
}
@keyframes errormsg {
  0% {
    opacity: 0;
    top: 60px;
  }
  10%{
    opacity: 1;
    top: 30px;
  }
  75%{
    opacity: 1;
    top: 30px;
  }
  100%{
    opacity: 0;
    top: 0px;
  }
}
@keyframes modalpop{
  0% {
    opacity: 0;
    top: calc(50% - 100px);
  }
  65%{
    opacity: 1;
    top: calc(50% - 240px);
  }
  100%{
    opacity: 1;
    top: calc(50% - 200px);
  }
}
@keyframes confirmModalPop{
  0% {
    opacity: 0;
    top: calc(50% - 100px);
  }
  65%{
    opacity: 1;
    top: calc(50% - 330px);
  }
  100%{
    opacity: 1;
    top: calc(50% - 300px);
  }
}
@keyframes branchModalPop{
  0% {
    opacity: 0;
    top: calc(50% - 100px);
  }
  65%{
    opacity: 1;
    top: calc(50% - 180px);
  }
  100%{
    opacity: 1;
    top: calc(50% - 150px);
  }
}
</style>
