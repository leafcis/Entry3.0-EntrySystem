<template>
  <div class="info-preview">
    <navigation />
    <headline v-if="!finalSubmit" :title="title" :subText="subText" />
    <headline v-else :title="'입학원서 출력'" :subText="subText" />
    <div class="pdf-target a4">
      <application
        :nowYear="nowYear"
        :nowMonth="nowMonth"
        :nowDay="nowDay"
        :finalSubmit="finalSubmit"/>
      <introduction class="introduction" :finalSubmit="finalSubmit"/>
      <academic-plan :finalSubmit="finalSubmit"/>
      <recommendation-letter
        v-if="admission !== 'NORMAL'"
        :nowYear="nowYear"
        :nowMonth="nowMonth"
        :nowDay="nowDay"
        :finalSubmit="finalSubmit"/>
      <quitting-smoking
        :nowYear="nowYear"
        :nowMonth="nowMonth"
        :nowDay="nowDay"
        :finalSubmit="finalSubmit"/>
    </div>
    <div class="btn-cover">
      <button
        class="input-btn"
        v-if="finalSubmit"
        @click="pagePrint">
        <span class="input-btn__text">모두 인쇄</span>
      </button>
    </div>
    <prev-next-btn
      v-if="!finalSubmit"
      :prevShow="true"
      :nextShow="true"
      :text="'최종 제출'"
      :prevLink="prevLink"
      :disablePrevClick="true"
      :onClick="confirm.bind(this)"/>
    <prev-next-btn
      v-else
      :prevShow="false"
      :nextShow="true"
      :text="'마이페이지'"
      :nextLink="'/mypage'"/>
    <entry-footer />
  </div>
</template>

<script>
import { mapState } from 'vuex';
import Navigation from '../common/Navigation';
import Headline from '../common/Headline';
import PrevNextBtn from '../common/PrevNextBtn';
import EntryFooter from '../common/EntryFooter';
import AcademicPlan from './AcademicPlan';
import Application from './Application';
import Introduction from './Introduction';
import QuittingSmoking from './QuittingSmoking';
import RecommendationLetter from './RecommendationLetter';

export default {
  name: 'preview',
  components: {
    Navigation,
    Headline,
    PrevNextBtn,
    EntryFooter,
    AcademicPlan,
    Application,
    Introduction,
    QuittingSmoking,
    RecommendationLetter,
  },
  data() {
    return {
      title: '원서 미리보기',
      subText: '대덕소프트웨어마이스터고등학교',
      prevLink: '/intro',
      nowYear: 0,
      nowMonth: 0,
      nowDay: 0,
    };
  },
  created() {
    const dateObj = (new Date());
    this.nowYear = dateObj.getFullYear();
    this.nowMonth = dateObj.getMonth() + 1;
    this.nowDay = dateObj.getDate();
    const token = this.$cookies.get('accessToken');
    const { e } = this.$toastr;
    if (token === undefined || token === null || token === '') {
      this.$router.go(-1);
      e('해당 페이지는 로그인이 필요합니다.');
      this.$store.commit('changeIndex', {
        index: 1,
      });
    }
    if (!this.isValid) {
      this.$router.go(-1);
      this.$store.commit('changeIndex', {
        index: 7,
      });
    }
  },
  computed: mapState({
    admission: state => state.classify.admission,
    isValid: state => state.mypage.validationResult.isValid,
    finalSubmit: state => state.mypage.applyStatus.finalSubmit,
  }),
  methods: {
    pagePrint() {
      const initBody = document.querySelector('.app');
      const pdfTarget = document.querySelector('.pdf-target');
      const pdfForPrint = pdfTarget.cloneNode(true);
      window.onbeforeprint = () => {
        initBody.style.display = 'none';
        pdfForPrint.classList.add('on');
        document.body.appendChild(pdfForPrint);
      };
      window.onafterprint = () => {
        initBody.style.display = 'block';
        document.body.removeChild(pdfForPrint);
      };
      window.print();
    },
    confirm() {
      this.$store.commit('changeIndex', {
        index: 8,
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import '../../style/setting';

@page a4sheet { size: 21.0cm 29.7cm }

.pdf-target {
  display: flex;
  flex-flow: column;
  width: 100%;
  &>div {
    zoom: 1.8;
    margin: 0 auto 30px;
    &:last-child {
      margin: 0 auto;
    }
  }
  &.on {
    position: absolute;
    top: 10px;
    left: 50%;
    transform: translateX(-50%);
  }
}

.info-preview {
  .pdf-target {
    background-color: #525659;
    width: 1140px;
    height: 1055px;
    overflow-y: scroll;
    margin: 0 auto;
    padding: 50px 0;
    &>div {
      zoom: 1.3;
      background-color: white;
    }
  }
}

$inner-btn: #296169;
.btn-cover {
  width: 1140px;
  height: 100px;
  margin: 0 auto;
  position: relative;

  .input-btn {
    position: absolute;
    top: 25px;
    width: 150px;
    height: 50px;
    line-height: 50px;
    border-radius: 5px;
    background-color: #f7fbfc;
    border: 1px solid #5f8a90;
    outline: none;
    cursor: pointer;
    transition: 0.5s;
    &:active {
      background-color: #D8E6E9;
    }
    right: 0;
    @include e('text') {
      font-size: 18px;
      color: $inner-btn;
      float: left;
      margin: {
        top: 2px;
        left: 14px;
      };
    }
  }
}
</style>
