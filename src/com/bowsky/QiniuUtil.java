package com.bowsky;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;

import java.io.File;
import java.util.Map;

/**
 * 七牛云存储工具类
 */
public class QiniuUtil {

	private static final String ACCESS_KEY = "dfKhFALDOkGqYYby2Gpc7eduAPxmeH9gQNV6M4HZ";
	private static final String SECRET_KEY = "MHMMeBlicaDoTEwgowHxB69irvq8CWnrTTmrL4vB";

	private static final String bucketname = "echang";
	public static final String baseUrl = "http://source.51echang.com/";
	//上传文件的路径
	String FilePath = "D://test.jpg";

	Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
	//创建上传对象
	UploadManager uploadManager = new UploadManager();

	//管理对象
	BucketManager bucketManager = new BucketManager(auth);

	//简单上传，使用默认策略，只需要设置上传的空间名就可以了
	public String getUpToken(){
		return auth.uploadToken(bucketname);
	}

	public  String  upload(File file) throws Exception {
		String url = "";
		try {
			//调用put方法上传
			Response res = uploadManager.put(file, file.getName(), getUpToken());
			//打印返回的信息
			System.out.println(res.statusCode);
			if (res.statusCode == 200)
			{
				Gson gson = new Gson();
				Map map = gson.fromJson(res.bodyString(), Map.class);
				url = url+baseUrl+map.get("key");
				System.out.println(url);
			}

		} catch (QiniuException e) {
			Response r = e.response;
			// 请求失败时打印的异常的信息
			System.out.println(r.toString());
			try {
				//响应的文本信息
				System.out.println(r.bodyString());

			} catch (QiniuException e1) {
				//ignore
			}
		}finally {
			file.delete();
		}
		return url;
	}


	public void stat(){
		//要测试的空间和key，并且这个key在你空间中存在
		String bucket = "Bucket_Name";
		String key = "Bucket_key";
		try {
			//调用stat()方法获取文件的信息
			FileInfo info = bucketManager.stat(bucket, key);
			System.out.println(info.hash);
			System.out.println(info.key);
		} catch (QiniuException e) {
			//捕获异常信息
			Response r = e.response;
			System.out.println(r.toString());
		}
	}

	public FileInfo[] list(){
		FileInfo[] items = null;
		//要测试的空间和key，并且这个key在你空间中存在
		String bucket = "echang";
		String key = "Bucket_key";
		try {
			//调用stat()方法获取文件的信息
			FileListing info = bucketManager.listFiles(bucket,null,null,1000,null);
			System.out.println(info.items.length);
			items = info.items;
		} catch (QiniuException e) {
			//捕获异常信息
			Response r = e.response;
			System.out.println(r.toString());
		}
		return items;
	}

	public static void main(String args[]) throws Exception{
		//new QiniuUtil().upload("http://www.haifenbei.com/includes/ueditor/php/../../../bdimages/upload1/20151120/1447984965120243.jpg");
		QiniuUtil qiniu = new QiniuUtil();
		//qiniu.stat();
		FileInfo[] items = qiniu.list();
		for (FileInfo info : items){
			System.out.printf("info:"+info.key);
		}
	}
}
