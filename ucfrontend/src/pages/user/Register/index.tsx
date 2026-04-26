import { register } from '@/services/ant-design-pro/api';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import { LoginForm, ProFormText } from '@ant-design/pro-components';
import { message } from 'antd';
import { history } from 'umi';
import styles from './index.less';
import React from 'react';

const Register: React.FC = () => {
  const handleSubmit = async (values: API.RegisterParams) => {
    try {
      // 1、两次密码是否一致校验，不一致则中止
      // 2、发起异步注册请求
      // 3、返回值（id）确认，大于0则提示成功消息，跳转至login页
      // 4、3以外则提示错误消息
      // 5、其他：发生异常情况提示错误消息
      const { userPassword, checkPassword } = values;
      if (userPassword !== checkPassword) {
        message.error('密码和确认密码不一致，请重新输入！');
        return;
      }
      // 注册
      const result = await register({ ...values });
      console.log(result);
      if (result.code === 20000 && result.data > 0) {
        message.success('注册成功');
        history.push('/user/login');
        return;
      }
      message.error(`${result.msg}/${result.desc}`);
    } catch (error) {
      message.error('注册失败，请重试！');
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <LoginForm
          submitter={{ searchConfig: { submitText: '注册' } }}
          title="注册用户"
          onFinish={async (values) => {
            await handleSubmit(values as API.RegisterParams);
          }}
        >
          <br />
          <br />

          <>
            <ProFormText
              name="userAccount"
              fieldProps={{
                size: 'large',
                prefix: <UserOutlined className={styles.prefixIcon} />,
              }}
              placeholder="请输入账户"
              rules={[
                {
                  required: true,
                  message: '账户是必填项！',
                },
                {
                  min: 5,
                  message: '账户长度不能少于5位',
                },
                {
                  max: 20,
                  message: '账户长度不能超过20位',
                },
              ]}
            />
            <ProFormText.Password
              name="userPassword"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined className={styles.prefixIcon} />,
              }}
              placeholder="请输入密码"
              rules={[
                {
                  required: true,
                  message: '密码是必填项！',
                },
                {
                  min: 8,
                  message: '密码长度不能少于8位',
                },
              ]}
            />
            <ProFormText.Password
              name="checkPassword"
              fieldProps={{
                size: 'large',
                prefix: <LockOutlined className={styles.prefixIcon} />,
              }}
              placeholder="请输入确认密码"
              rules={[
                {
                  required: true,
                  message: '确认密码是必填项！',
                },
              ]}
            />
          </>
        </LoginForm>
      </div>
    </div>
  );
};

export default Register;
