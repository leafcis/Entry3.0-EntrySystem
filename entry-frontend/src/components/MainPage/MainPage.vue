<template>
  <div class="main-page">
    <navigation
      @on-main="activeSchedulePage = isAppearSchedule = false"
      @on-schedule="activeSchedulePage = true"
      class="main-navigation"/>
    <div class="main-page__hider">
      <transition name="page1">
        <div class="main-page__cover main-page__cover--page1"
          v-if="!activeSchedulePage">
          <div class="main-page__content">
            <h4 class="main-page__sub-text">
              대덕소프트웨어마이스터고등학교
            </h4>
            <h1 class="main-page__title">
              2019 신입생 모집
            </h1>
            <div class="main-page__link-box">
              <div v-if="isLogin" @click="gotoWrite" class="main-page__link">
                원서작성
              </div>
              <div v-else @click="openBranchModal" class="main-page__link">
                원서작성
              </div>
              <a class="main-page__link"
                @click="activeSchedulePage = true">
                모집일정
              </a>
            </div>
          </div>
        </div>
      </transition>
      <transition name="page2" @after-enter="isAppearSchedule = true">
        <div class="main-page__cover main-page__cover--page2"
          v-if="activeSchedulePage">
          <div class="main-page__content">
            <h4 class="main-page__sub-text">
              대덕소프트웨어마이스터고등학교
            </h4>
            <h1 class="main-page__title">
              2019 신입생 모집일정
            </h1>
            <transition name="appear">
              <template v-if="isAppearSchedule">
                <div class="main-page__link-box">
                  <div v-if="isLogin" @click="gotoWrite" class="main-page__link">
                    원서작성
                  </div>
                  <div v-else @click="openBranchModal" class="main-page__link">
                    원서작성
                  </div>
                  <router-link to="/info-summary" class="main-page__link">
                    전형요강
                  </router-link>
                </div>
              </template>
            </transition>
            <schedule v-if="isAppearSchedule"
              @close="activeSchedulePage = isAppearSchedule = false"
              router-link="/auth"/>
          </div>
        </div>
      </transition>
    </div>
    <entry-footer />
  </div>
</template>

<script>
import { mapState } from 'vuex';
import Navigation from '../common/Navigation';
import EntryFooter from '../common/EntryFooter';
import Schedule from './Schedule';

export default {
  name: 'main-page',
  components: {
    Navigation,
    EntryFooter,
    Schedule,
  },
  data() {
    return {
      activeSchedulePage: false, // to animate
      isAppearSchedule: false, // to animatell
    };
  },
  computed: {
    isLogin() {
      const token = this.$cookies.get('accessToken');
      return this.$store.state.accessToken !== null && typeof token === 'string';
    },
    ...mapState({
      finalSubmit: state => state.mypage.applyStatus.finalSubmit,
    }),
  },
  methods: {
    openBranchModal() {
      this.$store.commit('changeIndex', {
       index: 10,
      });
    },
    gotoWrite() {
      if (this.finalSubmit) {
        this.$router.push('/mypage');
        return;
      }
      this.$router.push('/classify');
    },
  },
};
</script>

<style lang="scss" scoped>
@import '../../style/setting';

.main-page {
  .main-navigation { box-shadow: none; }

  background-image: url('../../assets/MainPage/main_background.svg');
  background-size: cover;
  background-position: center center;
  @include e('hider') {
    overflow-x: hidden;
    width: 100%;
    // 네비게이션, 푸터 제외
    height: calc(100vh - 60px);
    min-height: 750px;
    position: relative;
  }
  @include e('cover') {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    position: absolute;
    top: 0;
    @include m('page1') {
      right: 0;
    }
    @include m('page2') {
      left: 0;
    }
  }
  @include e('content') {
    width: 1140px;
    font-size: 0;
    display: flex;
    flex-wrap: wrap;
    align-content: flex-start;
    margin-bottom: 100px;
    border: {
      bottom: solid 6px #6ab7b7;
    }
  }
  @include e('sub-text') {
    height: 50px;
    width: 100%;
    font-size: 28px;
    font-weight: normal;
  }
  @include e('title') {
    height: 50px;
    line-height: 50px;
    font-size: 54px;
    font-weight: normal;
    flex: 1;
    margin: {
      bottom: 51px;
    }
  }
  @include e('link-box') {
    font-size: 0;
  }
  @include e('link') {
    height: 50px;
    display: inline-block;
    width: 175px;
    border-radius: 30px;
    background: -webkit-linear-gradient(left, #82cdca, #5db3b6);
    box-shadow: 1px 25px 20px -15px #9ff0eb;
    font-size: 22px;
    color: #fff;
    border: none;
    cursor: pointer;
    line-height: 50px;
    text-align: center;
    margin-left: 25px;
    text-decoration: none;
  }
}

// animation

.page1 {
  &-enter {
    right: 100%;
  }

  &-enter-to, &-leave {
    right: 0;
  }

  &-leave-to {
    right: 100%;
  }

  &-enter-active, &-leave-active {
    transition: right 1s cubic-bezier(0.8, 0, 0.4, 1);
  }
}

.page2 {
  &-enter {
    left: 100%;
  }

  &-enter-to, &-leave {
    left: 0;
  }

  &-leave-to {
    left: 100%;
  }

  &-enter-active, &-leave-active {
    transition: left 1s cubic-bezier(0.8, 0, 0.4, 1);
  }
}

.appear {
  &-enter {
    opacity: 0;
  }

  &-enter-to {
    opacity: 1;
  }

  &-enter-active {
    transition: opacity .5s;
  }
}
</style>
