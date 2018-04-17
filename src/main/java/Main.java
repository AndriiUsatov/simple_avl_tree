import tree.SimpleAVLTree;


public class Main {
    public static void main(String[] args) {

        SimpleAVLTree<Integer,String> tree = new SimpleAVLTree<Integer, String>();
        for (int i = 1; i < 11; i++) {
            System.out.println(tree.put(i,String.valueOf(i)));
        }
        System.out.println("Size of the tree is: " + tree.size());
        for (int i = 0; i < tree.size() ; i++) {
            System.out.println(tree.get(i));
        }
        tree.remove(5);
        for(SimpleAVLTree.Entry<Integer,String> entry : tree.entrySet()){
            System.out.println("Entry key: " + entry.getKey() + "\tEntry value: " + entry.getValue() );
        }
    }
}
