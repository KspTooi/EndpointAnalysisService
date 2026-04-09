package com.ksptool.bio.commons.utils;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ByteUtils {

    public static String byteToString(byte[] bytes,int length){

        StringBuilder sb = new StringBuilder();

        sb.append("[");

        for(int i=0;i<length;i++){

            if(i==length-1){
                sb.append(bytes[i]);
                continue;
            }
            sb.append(bytes[i]).append(",");
        }

        sb.append("]");

        return sb.toString();
    }

    public static byte[] filter(byte[] b,int length) {

        byte[] ret = new byte[length];


        if (length >= 0)
            System.arraycopy(b, 0, ret, 0, length);

        return ret;
    }


    public static List<Byte> byteToList(byte[] b){

        List<Byte> ret = new ArrayList<>();

        for(byte a:b){
            ret.add(a);
        }

        return ret;
    }

    /**
     * 将list转换为byte数组
     * @param list 集合
     * @return byte数组
     */
    public static byte[] listToByte(List<Byte> list){

        byte[] ret = new byte[list.size()];

        for(int i=0;i<ret.length;i++){
            ret[i] = list.get(i);
        }

        return ret;
    }

    //bytes合并
    public static byte[] merge(byte[] origin, byte[] target){

        byte[] ret = new byte[origin.length + target.length];

        System.arraycopy(origin,0,ret,0,origin.length);
        System.arraycopy(target,0,ret,origin.length,target.length);

        origin = ret;
        return ret;
    }


    //bytes分片
    public static List<byte[]> fragment(byte[] bytes,int fragmentLength){

        List<byte[]> ret = new ArrayList<>();

        int current = 0;

        for(int i=0; i < Math.ceil((double) bytes.length / fragmentLength); i++){

            byte[] frag = null;

            current = i * fragmentLength;

            int leftLength = bytes.length - (fragmentLength * (i));

            //最后一片处理
            if(i >= bytes.length / fragmentLength){
                frag = new byte[leftLength];
                System.arraycopy(bytes,current,frag,0,leftLength);
                ret.add(frag);
                break;
            }

            frag = new byte[fragmentLength];
            System.arraycopy(bytes,current,frag,0,fragmentLength);

            ret.add(frag);
        }

        return ret;
    }

    /**
     * 将byte切分成随机大小的块
     */
    public static List<byte[]> fragment(byte[] data,int minLength,int maxLength){

        List<byte[]> result = new ArrayList<>();

        int cur = 0;

        while(cur < data.length){

            int random = ByteUtils.random(minLength, maxLength);
            int end = (cur + random);

            if(end >= data.length){
                end = data.length;
            }

            result.add(range(data,cur,end-1));
            cur = cur + random;
        }

        return result;
    }



    public static byte[] genByte(int length){
        byte[] bytes = new byte[length];
        new Random().nextBytes(bytes);
        return bytes;
    }

    public static byte[] insertTail(byte[] data,long insert){
        return insertTail(data, toBytes(insert));
    }

    public static byte[] insertTail(byte[] data,int insert){
        return insertTail(data, toBytes(insert));
    }

    public static byte[] insertTail(byte[] data,short insert){
        return insertTail(data, toBytes(insert));
    }

    public static byte[] insertTail(byte[] data,byte insert){
        return insertTail(data, new byte[]{insert});
    }

    public static byte[] insertHead(byte[] data,byte insert){
        return insertHead(data, new byte[]{insert});
    }

    public static byte[] insertTail(byte[] data,byte[] insert){
        return merge(data, insert);
    }

    public static byte[] insertHead(byte[] data,byte[] insert){
        return merge(insert, data);
    }

    public static byte[] getTail(byte[] data,int length){
        byte[] tailResult = new byte[length];
        System.arraycopy(data,data.length - length,tailResult,0,length);
        return tailResult;
    }

    public static byte[] getHead(byte[] data,int length){
        byte[] headResult = new byte[length];
        System.arraycopy(data,0,headResult,0,length);
        return headResult;
    }

    public static byte[] range(byte[] data,int start,int end){

        if(start > end){
            throw new RuntimeException("start cannot be greater than end");
        }

        int rangeLength = (end - start) + 1;

        byte[] range = new byte[rangeLength];

        System.arraycopy(data,start,range,0,rangeLength);

        return range;
    }

    public static int random(int min,int max){
        Random rd = new Random();
        return rd.nextInt(max - min + 1) + min;
    }


    /**
     * 高斯分布函数
     * @param a 均值
     * @param v 方差
     */
    public static int gaussian(int a,int v){
        Random rd = new Random();
        return (int) (Math.sqrt(v) * rd.nextGaussian() + a);
    }

    public static byte[] toBytes(short v){
        return ByteBuffer.allocate(2).putShort(v).array();
    }

    public static byte[] toBytes(int v){
        return ByteBuffer.allocate(4).putInt(v).array();
    }

    public static byte[] toBytes(long v){
        return ByteBuffer.allocate(8).putLong(v).array();
    }

    public static short toShort(byte[] v){
        return ByteBuffer.allocate(2).put(v).getShort(0);
    }

    public static int toInt(byte[] v){
        return ByteBuffer.allocate(4).put(v).getInt(0);
    }

    public static long toLong(byte[] v){
        return ByteBuffer.allocate(8).put(v).getLong(0);
    }



}
