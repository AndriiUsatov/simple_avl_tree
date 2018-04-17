package tree;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class SimpleAVLTree<K, V> {
    private Comparator<K> comparator;
    private int size;
    private SimpleAVLTree<K, V>.Node<K, V> root;
    private Set<Entry<K, V>> entrySet;

    public SimpleAVLTree() {
    }

    public SimpleAVLTree(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    public boolean put(K key, V value) {
        if (key == null)
            return false;
        Node find = find(key);
        Node newNode = new Node(key, value);
        if (size == 0) {
            root = newNode;
        } else if (find == null || find.isLeaf()) {
            find.setValues(newNode);
            if (find.getParent() != null && find.getParent().getParent() != null && !find.getParent().getParent().isLeaf())
                rotate(find.getParent().getParent());
        } else {
            find.setValues(newNode);
            if (find.getParent() != null && find.getParent().getParent() != null && !find.getParent().getParent().isLeaf())
                rotate(find.getParent().getParent());
        }
        size++;
        return true;
    }

    public Set<Entry<K, V>> entrySet() {
        entrySet = new HashSet<Entry<K, V>>();
        if (root == null)
            return entrySet;
        else {
            Node<K, V> next = min(root);
            entrySet.add(new Entry<K, V>(next.key, next.value));
            while (true) {
                next = next.getNext();
                if (next.isLeaf())
                    break;
                entrySet.add(new Entry<K, V>(next.key, next.value));
            }
        }
        return entrySet;
    }

    public V get(K key) {
        if (key == null)
            throw new IllegalArgumentException();
        return (V) find(key).getValue();
    }

    public int size() {
        return size;
    }

    public boolean remove(K key) {
        if (key == null)
            throw new IllegalArgumentException(key.toString());
        Node removeNode = find(key);
        if (removeNode == null)
            return false;
        Node right = removeNode.getRight();
        Node left = removeNode.getLeft();
        Node parent = removeNode.getParent();

        if (right.isLeaf() && left.isLeaf()) {
            if (parent.getLeft() == removeNode) {
                parent.setLeft(new Node());
                parent.getLeft().setParent(parent);
            } else {
                parent.setRight(new Node());
                parent.getRight().setParent(parent);
            }
            rotate(parent);
        } else {
            if (right.isLeaf() && !left.isLeaf() || left.isLeaf() && !right.isLeaf()) {
                Node tmp = right.isLeaf() ? left : right;
                if (parent.getRight() == removeNode) {
                    parent.setRight(tmp);
                    tmp.setParent(parent);
                } else {
                    parent.setLeft(tmp);
                    tmp.setParent(parent);
                }
                if (removeNode == root)
                    root = tmp;
                rotate(tmp);
            } else {
                Node rightMin = min(right);
                if (rightMin == right) {
                    rightMin.setLeft(removeNode.getLeft());
                    rightMin.getLeft().setParent(rightMin);
                    rightMin.setParent(removeNode.getParent());
                } else {
                    rightMin.getParent().setLeft(rightMin.getRight());
                    rightMin.getRight().setParent(rightMin.getParent());
                    rightMin.setLeft(removeNode.getLeft());
                    rightMin.setRight(removeNode.getRight());
                    removeNode.getRight().setParent(rightMin);
                    removeNode.getLeft().setParent(rightMin);
                    rightMin.setParent(removeNode.getParent());
                }
                if (parent.getLeft() == removeNode)
                    parent.setLeft(rightMin);
                else
                    parent.setRight(rightMin);
                if (root == removeNode)
                    root = rightMin;
                rotate(rightMin);
            }
//            if(right.isLeaf()){
//                if(parent.getRight() == removeNode){
//                    parent.setRight(left);
//                    left.setParent(parent);
//                }else {
//                    parent.setLeft(left);
//                    left.setParent(parent);
//                }
//            }else if(left.isLeaf()){
//                if(parent.getRight() == removeNode){
//                    parent.setRight(right);
//                    right.setParent(parent);
//                }else {
//                    parent.setLeft(right);
//                    right.setParent(parent);
//                }
//            }
        }
        size--;
        if (size == 0)
            root = null;
        return true;
    }

    private Node find(K key) {
        Node result = null;
        Node currentNode = root;
        if (size == 0)
            return result;
        if (comparator != null) {
            while (result == null) {
                if (currentNode.isLeaf()) {
                    result = currentNode;
                    break;
                }
                int compareRes = comparator.compare(key, (K) currentNode.key);
                if (compareRes == 0) {
                    result = currentNode;
                    break;
                } else if (compareRes < 0)
                    currentNode = currentNode.left;
                else currentNode = currentNode.right;
            }
        } else {
            while (result == null) {
                if (currentNode.isLeaf()) {
                    result = currentNode;
                    break;
                }
                int compareRes = ((Comparable) key).compareTo(currentNode.key);
                if (compareRes == 0) {
                    result = currentNode;
                    break;
                } else if (compareRes < 0)
                    currentNode = currentNode.left;
                else currentNode = currentNode.right;
            }
        }
        return result;
    }

    private int countOfChildrens(Node node) {
        int result = 0;
        if (node.isLeaf())
            return result;
        if (!node.getLeft().isLeaf())
            result = node.getLeft().isLeaf() ? result : result + 1;
        if (!node.getRight().isLeaf())
            result = node.getRight().isLeaf() ? result : result + 1;
        if (result == 0)
            return result;
        else {
            return result + countOfChildrens(node.getLeft()) + countOfChildrens(node.getRight());
        }
    }

//    private void rotate(Node<K, V> node) {
//        if (node == null)
//            return;
//        while (!node.isLeaf()) {
//            int leftChilds = countOfChildrens(node.getLeft()) + (node.getLeft().isLeaf() ? 0 : 1);
//            int rightChilds = countOfChildrens(node.getRight()) + (node.getRight().isLeaf() ? 0 : 1);
//            if (leftChilds - rightChilds > 1) {
//                    if (!node.getLeft().getRight().isLeaf())
//                        rotationLR(node);
//                    else
//                        rotationLL(node);
//            }
//            if (leftChilds - rightChilds < -1) {
//                    if (!node.getRight().getRight().isLeaf())
//                        rotationRR(node);
//                    else
//                        rotationRL(node);
//            }
////            if(node.getParent() != null && node.getParent().getParent() != null && !node.getParent().getParent().isLeaf())
//            node = node.getParent();
//        }
//    }

    private void rotate(Node<K, V> node) {
        if (node == null)
            return;
        while (!node.isLeaf()) {
            int leftChilds = countOfChildrens(node.getLeft()) + (node.getLeft().isLeaf() ? 0 : 1);
            int rightChilds = countOfChildrens(node.getRight()) + (node.getRight().isLeaf() ? 0 : 1);
            if (leftChilds - rightChilds > 1) {
                int nextLeftCount = countOfChildrens(node.getLeft().getLeft()) + (node.getLeft().getLeft().isLeaf() ? 0 : 1);
                int nextRightCount = countOfChildrens(node.getLeft().getRight()) + (node.getLeft().getRight().isLeaf() ? 0 : 1);
                if (nextLeftCount - nextRightCount > 1)
                    rotationLL(node);
                else
                    rotationLR(node);
            }
            if (leftChilds - rightChilds < -1) {
                int nextLeftCount = countOfChildrens(node.getRight().getLeft()) + (node.getRight().getLeft().isLeaf() ? 0 : 1);
                int nextRightCount = countOfChildrens(node.getRight().getRight()) + (node.getRight().getRight().isLeaf() ? 0 : 1);
                if (nextLeftCount - nextRightCount > 1)
                    rotationRL(node);
                else
                    rotationRR(node);
            }
//            if(node.getParent() != null && node.getParent().getParent() != null && !node.getParent().getParent().isLeaf())
            node = node.getParent();
        }
    }

    private void rotationLL(Node<K, V> node) {
        Node left = node.getLeft();
        Node nodeParent = node.getParent();

        Node tmp = left.getRight();

        left.setRight(node);
        left.setParent(nodeParent);
        node.setLeft(tmp);
        node.getLeft().setParent(node);
        node.setParent(left);
        if (left.getParent().getLeft() == node)
            left.getParent().setLeft(left);
        if (left.getParent().getRight() == node)
            left.getParent().setRight(left);
        if (node == root)
            root = left;
    }

    private void rotationRR(Node<K, V> node) {
        Node right = node.getRight();
        Node nodeParent = node.getParent();

        Node tmp = right.getLeft();

        right.setLeft(node);
        right.setParent(nodeParent);
        node.setRight(tmp);
        node.getRight().setParent(node);
        node.setParent(right);
        if (right.getParent().getLeft() == node)
            right.getParent().setLeft(right);
        if (right.getParent().getRight() == node)
            right.getParent().setRight(right);
        if (node == root)
            root = right;
    }

    private void rotationLR(Node<K, V> node) {
        Node leftRight = node.getLeft().getRight();
        Node nodeParent = node.getParent();
        Node left = node.getLeft();

        Node tmp = leftRight.getRight();

        leftRight.setRight(node);
        leftRight.setLeft(left);
        leftRight.setParent(nodeParent);

        left.setParent(leftRight);

        left.setRight(new Node());
//        left.setRight(tmp);

        left.getRight().setParent(left);

        node.setLeft(tmp);
        node.getLeft().setParent(node);
        node.setParent(leftRight);
        if (leftRight.getParent().getLeft() == node)
            leftRight.getParent().setLeft(leftRight);
        if (leftRight.getParent().getRight() == node)
            leftRight.getParent().setRight(leftRight);
        if (node == root)
            root = leftRight;
    }

    private void rotationRL(Node<K, V> node) {
        Node rightLeft = node.getRight().getLeft();
        Node nodeParent = node.getParent();
        Node right = node.getRight();

        Node tmp = rightLeft.getLeft();

        rightLeft.setLeft(node);
        rightLeft.setRight(right);
        rightLeft.setParent(nodeParent);

        right.setParent(rightLeft);
//        right.setLeft(new Node());
//        Changed to this one
        right.setLeft(tmp);

        right.getLeft().setParent(right);

        node.setRight(new Node());
        node.getRight().setParent(node);
        node.setParent(rightLeft);
        if (rightLeft.getParent().getLeft() == node)
            rightLeft.getParent().setLeft(rightLeft);
        if (rightLeft.getParent().getRight() == node)
            rightLeft.getParent().setRight(rightLeft);
        if (node == root)
            root = rightLeft;
    }

    private Node min(Node node) {
        if (node.isLeaf())
            return node;
        if (node.getLeft().isLeaf())
            return node;
        else return min(node.getLeft());
    }

    private Node max(Node node) {
        if (node.isLeaf())
            return node;
        if (node.getRight().isLeaf())
            return node;
        else return max(node.getRight());
    }

    private class Node<K, V> {
        private K key;
        private V value;
        private int balanceFactor;
        private Node left;
        private Node right;
        private Node parent;

        private Node() {
        }

        private Node(K key, V value) {
            this.key = key;
            this.value = value;
            left = setLeaf();
            left.setParent(this);
            right = setLeaf();
            right.setParent(this);
            parent = setLeaf();
        }

        private K getKey() {
            return key;
        }

        private V getValue() {
            return value;
        }

        private boolean hasLeft() {
            return !left.isLeaf();
        }

        private boolean hasRight() {
            return !right.isLeaf();
        }

        private boolean hasParent() {
            return !parent.isLeaf();
        }

        private Node getLeft() {
            if (left == null)
                left = setLeaf();
            return left;
        }

        private Node getRight() {
            if (right == null)
                right = setLeaf();
            return right;
        }

        private Node getParent() {
            return parent;
        }

        private Node<K, V> setLeaf() {
            return new Node<K, V>();
        }

        private void setLeft(Node node) {
            left = node;
        }

        private void setRight(Node node) {
            right = node;
        }

        private void setParent(Node node) {
            parent = node;
        }

        private boolean isLeaf() {
            return this == null || this.key == null;
        }

        private Node getNext() {
            Node result = null;
            Node currentNode = this;
            if (!currentNode.getRight().isLeaf()) {
                result = currentNode.getRight();
                while (!result.getLeft().isLeaf())
                    result = result.getLeft();
                return result;
            }
            result = currentNode.getParent();
            while (!result.isLeaf() && currentNode == result.getRight()) {
                currentNode = result;
                result = result.getParent();
            }
            return result;
        }

        private void setValues(Node<K, V> node) {
            if (this.isLeaf()) {
                setLeft(node.getLeft());
                node.getLeft().setParent(this);
                setRight(node.getRight());
                node.getRight().setParent(this);

            }
            this.key = node.getKey();
            this.value = node.value;
        }
    }

    public static class Entry<K, V> {
        private K key;
        private V value;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
}
