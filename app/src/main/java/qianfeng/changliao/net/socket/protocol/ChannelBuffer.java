package qianfeng.changliao.net.socket.protocol;

/**
 * Created by wukai on 15/12/8.
 * //链接接收数据的缓存
 * channelbuf为一个环形缓冲区
 */
public class ChannelBuffer {

    //缓存容量 字节数
    private int mLen;
    //读位置
    private int mReadIndex = 0;
    //写位置
    private int mWriteIndex = 0;

    //字节内容
    private byte[] array;


    public ChannelBuffer(int max){
        mLen = max;
        array = new byte[mLen];
        mWriteIndex = 0;
        mReadIndex = 0;
    }

    //写入数据
    public boolean writeData(byte[] data){

        synchronized (this){
            if(getAvailableWriteLen()<data.length) return false;
            for (int i = 0; i < data.length; i++) {
                //当writeindex等于mLen的时候读指针移动到0位置
                if (mWriteIndex ==mLen) mWriteIndex =0;
                array[mWriteIndex] = data[i];
                mWriteIndex++;
            }
            return true;
        }

    }

    //从缓存中拉取数据
    public byte[] readData(int len){
        byte[] data = null;
        synchronized (this){
            if (getAvailableReadLen()<len)return null;
            data = new byte[len];
            //字节复制
            int index = mReadIndex;
            for (int i = 0; i < len; i++) {
                if (index==mLen)index =0;
                data[i] = array[index];
                index++;
            }
        }
        return data;
    }

    /**
     * 获取可写的长度
     * @return
     */
    public int getAvailableWriteLen(){
        int len = mLen;
        synchronized (this){
            //当写标记大于读标记的时候那么缓存的可用写长度为
            if (mReadIndex < mWriteIndex){
                len = mLen-(mWriteIndex - mReadIndex);
            }else if (mReadIndex > mWriteIndex){
                len = mReadIndex - mWriteIndex;
            }
        }
        return len;
    }

    /**
     * 获取可读的字节长度
     * @return
     */
    public int getAvailableReadLen(){
        int len = 0;
        synchronized (this){
            if (mReadIndex < mWriteIndex){
                len = mWriteIndex - mReadIndex;
            }else if (mReadIndex > mWriteIndex){
                len = mLen-(mReadIndex - mWriteIndex);
            }
        }
        return len;
    }

    //移动读指针的位置
    public boolean moveReadPostion(int len){
        synchronized (this){
        for (int i = 0; i <len ; i++) {
            if (mReadIndex == mLen)mReadIndex =0;
            mReadIndex++;
        }
        }
        return true;
    }

}
