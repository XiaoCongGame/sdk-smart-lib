/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 */

package com.xiaocong.alipayjo;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.alipay.android.app.IAlixPay;
import com.alipay.android.app.IRemoteServiceCallback;






/**
 * �Ͱ�ȫ֧������ͨ�ţ����Ͷ�����Ϣ����֧��������֧����������Ϣ
 * 
 */
public class MobileSecurePayer {
	static String TAG = "MobileSecurePayer";

	Integer lock = 0;
	IAlixPay mAlixPay = null;
	boolean mbPaying = false;

	Activity mActivity = null;

	//�Ͱ�ȫ֧������bl��
	private ServiceConnection mAlixPayConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			//
			// wake up the binder to continue.
			// ���ͨ��ͨ��
			synchronized (lock) {
				mAlixPay = IAlixPay.Stub.asInterface(service);
				lock.notify();
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			mAlixPay = null;
		}
	};

	/**
	 * ��֧��������֧������
	 * 
	 * @param strOrderxc_info
	 *            ������Ϣ
	 * @param callback
	 *            �ص�handler
	 * @param myWhat
	 *            �ص���Ϣ
	 * @param activity
	 *            Ŀ��activity
	 * @return
	 */
	public boolean pay(final String strOrderxc_info, final Handler callback,
			final int myWhat, final Activity activity) {
		if (mbPaying)
			return false;
		mbPaying = true;

		//
		mActivity = activity;

		// bind the service.
		// �󶨷���
		if (mAlixPay == null) {
			// �󶨰�ȫ֧��������Ҫ��ȡ�����Ļ�����
			// ���󶨲��ɹ�ʹ��mActivity.getApplicationContext().bindService
		    // ���ʱͬ��
			mActivity.getApplicationContext().bindService(
					new Intent(IAlixPay.class.getName()), mAlixPayConnection,
					Context.BIND_AUTO_CREATE);
		}
		// else ok.

		// ʵ��һ���߳�4����֧��
		new Thread(new Runnable() {
			public void run() {
				try {
					// wait for the service bind operation to completely
					// finished.
					// Note: this is important,otherwise the next mAlixPay.Pay()
					// will fail.
					// �ȴ�ȫ֧������󶨲������
					// ע�⣺�������Ҫ������mAlixPay.Pay()������ʧ��
					synchronized (lock) {
						if (mAlixPay == null)
							lock.wait();
					}

					// register a Callback for the service.
					// Ϊ��ȫ֧������ע��һ��ص�
					mAlixPay.registerCallback(mCallback);

					// call the MobileSecurePay service.
					// ���ð�ȫ֧�������pay����
					String strRet = mAlixPay.Pay(strOrderxc_info);
					BaseHelper.log(TAG, "After Pay: " + strRet);

					// set the flag to indicate that we have finished.
					// unregister the Callback, and unbind the service.
					// ��mbPaying��Ϊfalse����ʾ֧������
					// �Ƴ�ص��ע�ᣬ���ȫ֧������
					mbPaying = false;
					mAlixPay.unregisterCallback(mCallback);
					mActivity.getApplicationContext().unbindService(
							mAlixPayConnection);

					// send the result back to caller.
					// ���ͽ��׽��
					Message msg = new Message();
					msg.what = myWhat;
					msg.obj = strRet;
					callback.sendMessage(msg);
				} catch (Exception e) {
					e.printStackTrace();

					// send the result back to caller.
					// ���ͽ��׽��
					Message msg = new Message();
					msg.what = myWhat;
					msg.obj = e.toString();
					callback.sendMessage(msg);
				}
			}
		}).start();

		return true;
	}

	/**
	 * This implementation is used to receive callbacks from the remote service.
	 * ʵ�ְ�ȫ֧���Ļص�
	 */
	private IRemoteServiceCallback mCallback = new IRemoteServiceCallback.Stub() {
		/**
		 * This is called by the remote service regularly to tell us about new
		 * values. Note that IPC calls are dispatched through a thread pool
		 * running in each process, so the code executing here will NOT be
		 * running in our main thread like most other things -- so, to update
		 * the UI, we need to use a Handler to hop over there.ͨ��IPC�������ȫ֧������
		 */
		public void startActivity(String packageName, String className,
				int iCallingPid, Bundle bundle) throws RemoteException {
			Intent intent = new Intent(Intent.ACTION_MAIN, null);

			if (bundle == null)
				bundle = new Bundle();
			// else ok.

			try {
				bundle.putInt("CallingPid", iCallingPid);
				intent.putExtras(bundle);
			} catch (Exception e) {
				e.printStackTrace();
			}

			intent.setClassName(packageName, className);
			mActivity.startActivity(intent);
		}

		@Override
		public boolean isHideLoadingScreen() throws RemoteException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void payEnd(boolean arg0, String arg1) throws RemoteException {
			// TODO Auto-generated method stub
			
		}


	};
}