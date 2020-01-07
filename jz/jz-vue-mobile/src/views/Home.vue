<template>
    <div class="home">
        <div class="title">请准确填写下面相关内容，有问题联系招生老师</div>
        <van-cell-group>
            <van-field v-model="userInfo.name" label="姓名:" placeholder="请输入姓名" required />
            <van-cell :value="userInfo.gender" title="性别:" is-link @click="genderPickerVisible = true" required />
            <van-cell :value="userInfo.birthday" title="出身日期:" is-link @click="showCalendar(1)" required />
            <van-field v-model="userInfo.idCardNo" label="身份证号码:" placeholder="请输入身份证号码" required />
            <van-field v-model="userInfo.mobile" label="手机号码:" placeholder="请输入手机号码" required />
            <van-switch-cell v-model="userInfo.medicalBackground" title="是否有医学背景:" required />
            <van-cell :value="userInfo.educationLevel" title="文化程度:" is-link @click="educationLevelsPickerVisible = true" required />
            <van-cell :value="userInfo.graduationDate" title="毕业年限:" is-link @click="showCalendar(2)" required />
            <van-field v-model="userInfo.graduationMajor" label="毕业专业:" placeholder="请输入毕业专业" required />
            <van-field v-model="userInfo.graduationSchool" label="毕业院校:" placeholder="请输入毕业院校" required />
            <van-field v-model="userInfo.workUnit" label="工作单位:" placeholder="请输入工作单位" required />
            <van-cell title="工作年限:" required>
                <van-stepper v-model="userInfo.workYear" />
            </van-cell>
            <van-field v-model="userInfo.job" label="从事职业:" placeholder="请输入从事职业" required />
            <van-field v-model="userInfo.workUnitAddress" label="单位地址:" placeholder="请输入单位地址" required />
            <van-field v-model="userInfo.workExperience" label="工作经历:" type="textarea" rows="1" autosize placeholder="起止时间、单位名称、职务、证明人" required />
        </van-cell-group>

        <div class="title">请上传报名资料电子版（照片, 最大 5M）资料</div>
        <div class="files">
            <van-cell-group>
                <van-cell title="2 寸白底照片:" center required><Uploader v-model="userInfo.picture" /></van-cell>
                <van-cell title="毕业证照片:" center required><Uploader v-model="userInfo.graduationCertificate" /></van-cell>
                <van-cell title="个人申请表:" center required><Uploader v-model="userInfo.applicationForm" /></van-cell>
                <van-cell title="身份证正面照片:" center required><Uploader v-model="userInfo.idCardPictureFront" /></van-cell>
                <van-cell title="身份证反面照片:" center required><Uploader v-model="userInfo.idCardPictureBack" /></van-cell>
                <van-cell title="工作证明:" center required><Uploader v-model="userInfo.workCertificate" /></van-cell>
                <van-cell title="考生承诺书:" center required><Uploader v-model="userInfo.commitmentLetter" /></van-cell>
                <van-cell title="学信网证明:" center><Uploader v-model="userInfo.xxwCertificate" /></van-cell>
            </van-cell-group>
        </div>

        <div class="buttons">
            <van-button type="info" @click="save">提交</van-button>
            <van-button @click="reset">清除</van-button>
        </div>

        <!-- 日期、性别、学历选择器 -->
        <van-popup v-model="calendarVisible" position="bottom">
            <van-datetime-picker type="date" :min-date="minDate" :max-date="maxDate" @cancel="calendarVisible = false" @confirm="selectDate" />
        </van-popup>

        <van-popup v-model="genderPickerVisible" position="bottom">
            <van-picker show-toolbar :columns="genders" @cancel="genderPickerVisible = false" @confirm="selectGender"/>
        </van-popup>

        <van-popup v-model="educationLevelsPickerVisible" position="bottom">
            <van-picker show-toolbar :columns="educationLevels" @cancel="educationLevelsPickerVisible = false" @confirm="selectEducationLevel"/>
        </van-popup>
    </div>
</template>

<script>
import Uploader from '@/components/Uploader';

function newUserInfo() {
    return {
        name             : '', // 姓名
        gender           : '', // 性别
        birthday         : '', // 出身日期
        idCardNo         : '', // 身份证号码
        mobile           : '', // 手机号码
        medicalBackground: true, // 医学背景
        educationLevel   : '', // 文化程度
        graduationDate   : '', // 毕业年限
        graduationMajor  : '', // 毕业专业
        graduationSchool : '', // 毕业院校
        workUnit         : '', // 工作单位
        workYear         : 1,  // 工作年限
        job              : '', // 从事职业
        workUnitAddress  : '', // 单位地址
        workExperience   : '', // 工作经历

        picture              : '', // 2 寸白底照片
        graduationCertificate: '', // 毕业证照片
        applicationForm      : '', // 个人申请表
        idCardPictureFront   : '', // 身份证正面照片
        idCardPictureBack    : '', // 身份证反面照片
        workCertificate      : '', // 工作证明
        commitmentLetter     : '', // 承诺书
        xxwCertificate       : '', // 学信网证明
    };
}

export default {
    components: {
        Uploader
    },
    data() {
        return {
            userInfo       : newUserInfo(),
            calendarFlag   : 0,     // 日历的类型
            calendarVisible: false, // 日历是否可见

            genderPickerVisible: false, // 性别选择器是否可见
            genders: ['男', '女', '其他'],

            educationLevels: ['大学本科', '研究生硕士', '研究生博士', '大学专科', '中等专业学校', '技工学校', '高中', '初中', '小学', '其他'],
            educationLevelsPickerVisible: false, // 学历选择器是否可见

            minDate: new Date(1920, 0, 1),
            maxDate: new Date(2050, 0, 31),
        };
    },
    methods: {
        save() {
            if (this.validate()) {
                Rest.create({ url: '/api/application', data: this.userInfo, json: true }).then(result => {
                    this.$toast.success('提交成功');
                });
            }
        },
        validate() {
            var rules = [
                { prop: 'name', message: '请输入姓名' },
                { prop: 'gender', message: '请选择性别' },
                { prop: 'birthday', message: '请选择出身日期' },
                { prop: 'idCardNo', message: '请输入身份证号码' },
                { prop: 'mobile', message: '请输入手机号码' },
                { prop: 'educationLevel', message: '请选择文化程度' },
                { prop: 'graduationDate', message: '请选择毕业年限' },
                { prop: 'graduationMajor', message: '请输入毕业专业' },
                { prop: 'graduationSchool', message: '请输入毕业院校' },
                { prop: 'workUnit', message: '请输入工作单位' },
                { prop: 'job', message: '请输入从事职业' },
                { prop: 'workUnitAddress', message: '请输入单位地址' },
                { prop: 'workExperience', message: '请输入工作经历' },
                { prop: 'picture', message: '请上传 2 寸白底照片' },
                { prop: 'graduationCertificate', message: '请上传毕业证照片' },
                { prop: 'applicationForm', message: '请上传个人申请表' },
                { prop: 'idCardPictureFront', message: '请上传身份证正面照片' },
                { prop: 'idCardPictureBack', message: '请上传身份证反面照片' },
                { prop: 'workCertificate', message: '请上传工作证明' },
                { prop: 'commitmentLetter', message: '请上传考生承诺书' },
            ];

            for (let rule of rules) {
                if (!this.userInfo[rule.prop]) {
                    this.$toast.fail(rule.message);
                    return false;
                }
            }

            return true;
        },
        selectDate(date) {
            this.calendarVisible = false;

            if (this.calendarFlag === 1) {
                // 出身日期
                this.userInfo.birthday = dayjs(date).format('YYYY-MM-DD');
            } else if (this.calendarFlag === 2) {
                // 毕业年限
                this.userInfo.graduationDate = dayjs(date).format('YYYY-MM-DD');
            }
        },
        selectEducationLevel(value) {
            this.userInfo.educationLevel = value;
            this.educationLevelsPickerVisible = false;
        },
        selectGender(value) {
            this.userInfo.gender = value;
            this.genderPickerVisible = false;
        },
        showCalendar(type) {
            this.calendarFlag = type;
            this.calendarVisible = true;
        },
        reset() {
            this.userInfo = newUserInfo();
        }
    }
};
</script>

<style lang="scss">
.home {
    .title {
        margin: 0;
        padding: 16px;
        color: rgba(69, 90, 100, 0.6);
        background-color: #f7f8fa;
        font-weight: normal;
        font-size: 14px;
        line-height: 14px;
    }

    .buttons {
        display: grid;
        grid-template-columns: 1fr 100px;
        grid-gap: 6px;
        padding: 6px;
    }
}
</style>
