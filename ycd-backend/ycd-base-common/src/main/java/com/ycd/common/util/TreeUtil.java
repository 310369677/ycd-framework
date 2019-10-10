package com.ycd.common.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TreeUtil {


    public interface TreeNode {
        /**
         * 节点的id,唯一标识符
         *
         * @return 树的id
         */
        String nodeId();

        /**
         * 父级id
         *
         * @return 父级的id
         */
        String parentId();

        /**
         * 孩子列表
         *
         * @return 孩子的列表
         */
        <T extends TreeNode> List<T> children();

        /**
         * 是否为叶子节点
         *
         * @return 是否为叶子节点
         */
        boolean leaf();
    }


    /**
     * 组装树
     *
     * @param head     头
     * @param nodeList 孩子列表
     * @param <T>      泛型
     */

    public static <T extends TreeNode> void makeTree(T head, List<T> nodeList, Comparator<TreeNode> comparator) {
        if (SimpleUtil.isEmpty(nodeList) || SimpleUtil.isEmpty(head)) {
            return;
        }
        CopyOnWriteArrayList<T> wrapList = new CopyOnWriteArrayList<>(nodeList);
        boolean hasChildren = false;
        for (T t : wrapList) {
            if (head.nodeId().equals(t.parentId())) {
                List<T> list = head.children();
                list.add(t);
                wrapList.remove(t);
                hasChildren = true;
            }
        }
        if (!hasChildren) {
            return;
        }
        if (SimpleUtil.isNotEmpty(comparator)) {
            head.children().sort(comparator);
        }
        List<T> childrenList = head.children();
        //链接孩子
        for (T childTreeNode : childrenList) {
            makeTree(childTreeNode, wrapList, comparator);
        }
    }

    public static <T extends TreeNode> void makeTree(T head, List<T> nodeList) {
        makeTree(head, nodeList, null);
    }

    public static <T extends TreeNode> T findNode(T head, String findId) {
        if (SimpleUtil.isEmpty(head) || SimpleUtil.isEmpty(findId)) {
            return null;
        }
        if (findId.equals(head.nodeId())) {
            return head;
        }
        List<T> childrenList = head.children();
        //从孩子查找
        for (T child : childrenList) {
            T result = findNode(child, findId);
            if (SimpleUtil.isNotEmpty(result)) {
                return result;
            }
        }
        return null;
    }


    /**
     * 将一颗树转换成列表
     *
     * @param head 头
     * @param <T>  泛型
     * @return 结果列表
     */
    public static <T extends TreeNode> List<T> convert2List(T head) {
        List<T> result = new ArrayList<>();
        if (SimpleUtil.isNotEmpty(head)) {
            result.add(head);
        }
        List<T> childrenList = head.children();
        //将孩子增加进去
        for (T child : childrenList) {
            result.addAll(convert2List(child));
        }
        return result;
    }


}
