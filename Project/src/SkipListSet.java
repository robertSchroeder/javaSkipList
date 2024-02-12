import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class SkipListSet<T extends Comparable<T>> implements SortedSet<T> 
{
   class SkipListSetIterator<T extends Comparable<T>> implements Iterator<T>
   {

   //The top level of the skip list(SL) is always empty; when not empty, all new items start at base.
   //Items located at heigher level than base will be between base level, and the empty top level.

      private SkipListSetItem<T> head;
      private SkipListSetItem<T> tail;

      private int height = 0; // keeps track of the height of the skip list
      private int itemCount=0;

   // Constructor for an brand new, empty skip list:

      public SkipListSetIterator() 
      {
         head = new SkipListSetItem<>();
         tail = new SkipListSetItem<>();

         // Now we have these point to each other because the SL is empty:
         head.setNext(tail);
         tail.setPrev(head);

         head.setIsHead(true);
         head.setIsTail(false);

         tail.setIsHead(false);
         tail.setIsTail(true);

      }

   //Constructor that accepts a collection and populates the SL with its elements:

      public SkipListSetIterator(Collection<T> collection)
      {
         head = new SkipListSetItem<>();
         tail = new SkipListSetItem<>();

         // Now we have these point to each other because the SL is empty:
         head.setNext(tail);
         tail.setPrev(head);

         head.setIsHead(true);
         head.setIsTail(false);

         tail.setIsHead(false);
         tail.setIsTail(true);

         insertCollection(collection);

      }

   // Search function: returns the target SkipListSetItem located at the base level or the SkipListSetItem with a data
   // value closest, but not greater than the key. Returns top head SkipListSetItem if SL is empty.

      public SkipListSetItem<T> search(T key)
      {
         SkipListSetItem<T> node=head;

         while (node.getBelow() != null) 
         {
            node = node.getBelow();

            while(!node.getNext().getIsTail() && node.getNext().getData().compareTo(key) <= 0) node=node.getNext();
         }

         return node;
      }

   //addNewLevel: adds a new layer on top of the existing top layer.

      public void addNewLevel()
      {
         SkipListSetItem<T> newHead= new SkipListSetItem<>();
         SkipListSetItem<T> newTail= new SkipListSetItem<>();

         newHead.setIsHead(true);
         newTail.setIsTail(true);

         newHead.setNext(newTail);
         newTail.setPrev(newHead);

         head.setAbove(newHead);
         tail.setAbove(newTail);
         
         newHead.setBelow(head);
         newTail.setBelow(tail);

         head=newHead;
         tail=newTail;

         height++;

      }

   // addLayerCheck: determines whether a new layer must be added and calls a method to do so if true.
   
      public void addLayerCheck(int level) 
      {
         if (level >= height)addNewLevel();// means that we need more layers
      }

   // insertBottom: inserts the new nodes at the base level of the SL, and returns the new node.

      public SkipListSetItem<T> insertBottom(SkipListSetItem<T> currentNode, T key) 
      {
         SkipListSetItem<T> newNode = new SkipListSetItem<>(key);

         currentNode.getNext().setPrev(newNode);
         newNode.setNext(currentNode.getNext());
         newNode.setPrev(currentNode);
         currentNode.setNext(newNode);

         return newNode;
      }

   // insertNextLevel: inserts a node at the next layer of the SL, and returns the new node.
   
      public SkipListSetItem<T> insertNextLevel(SkipListSetItem<T> aboveNode, SkipListSetItem<T> newBaseNode, T key)
      {  

         SkipListSetItem<T> newNode = new SkipListSetItem<>(key);

         aboveNode.getNext().setPrev(newNode);
         newNode.setNext(aboveNode.getNext());
         newNode.setPrev(aboveNode);
         aboveNode.setNext(newNode);

         newBaseNode.setAbove(newNode);
         newNode.setBelow(newBaseNode);
         
         return newNode;
      }

      //coinFlip: determines whether a node should be inserted at a heigher level.

      public void coinFlip(SkipListSetItem<T> foundNode,SkipListSetItem<T> newNode, T key)
      {
         //How height randomization works:
         // For the following loop: random.nextDouble() will return a value between 0 and 1.
         // During the first iteration, there is a 50% chance that it will return a value less than 0.5.
         // On the next iteration it must return a value less than 0.25 , which is half the initial probability.
         // Continues as long as probability is greater than 0 or the the random double returned is larger than probability.

         int levels=-1; // keeps track of the amount of levels we are adding the new node in.
         double probability=0.5; // Used to determine whether we'll add the new node at a heigher level.

         Random random = new Random();

         while(random.nextDouble()<probability && probability>=0.01) 
         {
            levels++;
            addLayerCheck(levels);

            while(foundNode.getAbove()==null)foundNode=foundNode.getPrev();
            
            foundNode=foundNode.getAbove();
            newNode=insertNextLevel(foundNode, newNode, key);
            probability*=0.5; // we reduce the probability of adding another level by half after each iteration.

         }

      }

   // Insert method that adds values one at a time:

      public Boolean addItem(T key) 
      {
         SkipListSetItem<T> foundNode = search(key);// if the SL is empty, it will return the top head node.

         if(!foundNode.getIsHead() && foundNode.getData().compareTo(key) == 0)return false;
            
         if(height<1) addNewLevel();
      
         SkipListSetItem<T> newNode=insertBottom(foundNode,key);
         itemCount++;

         coinFlip(foundNode,newNode,key);

         return true;
      }

      public Boolean deleteItem(T key) 
      {
         SkipListSetItem<T> foundNode=search(key);
         SkipListSetItem<T> leftAdjacent;
         SkipListSetItem<T> rightAdjacent;

         if(foundNode.getIsHead() || foundNode.getData().compareTo(key) != 0) return false;
            
         while(foundNode!=null) 
         {
            leftAdjacent=foundNode.getPrev();
            rightAdjacent=foundNode.getNext();

            leftAdjacent.setNext(rightAdjacent);
            rightAdjacent.setPrev(leftAdjacent);

            foundNode=foundNode.getAbove();
         }

         return true;

      }

   // baseHead(): locates the head node at the base level and returns it.

      public SkipListSetItem<T> baseHead() 
      {
         SkipListSetItem<T> baseHead = head;

         while (baseHead.getBelow() != null) 
         {
            baseHead = baseHead.getBelow();
         }

         return baseHead;

      }

      public SkipListSetItem<T> baseTail() 
      {
         SkipListSetItem<T> baseTail = tail;

         while (baseTail.getBelow() != null) 
         {
            baseTail = baseTail.getBelow();
         }

         return baseTail;

      }

   // collectItems: saves the data from each node at the base level into an
   // ArrayList and returns it.

      public ArrayList<T> collectItems() 
      {
         SkipListSetItem<T> node = baseHead(); // node starts by holding the address of the head node at the base level.
         ArrayList<T> list = new ArrayList<>();
         int index = 0;

         if (node.getNext().getIsTail())return null; // this means the SL is empty.
            
         else node = node.getNext(); // If it's not empty, now we're at a none-sentinel node in the list.
            
         while (!node.getIsTail()) 
         {
            list.add(index, node.getData());
            index++;
            node = node.getNext();
         }

         return list;
      }

      // reSetList: resets the head and tails pointers so that we can re-balance the SL

      public void reSetList(SkipListSetItem<T> baseHead,SkipListSetItem<T> baseTail) 
      {
         SkipListSetItem<T> newHead = new SkipListSetItem<>();
         SkipListSetItem<T> newTail = new SkipListSetItem<>();

         newHead.setNext(newTail);
         newTail.setPrev(newHead);

         newHead.setIsHead(true);
         newTail.setIsTail(true);

         head = newHead;
         tail = newTail;

         head.setBelow(baseHead);
         tail.setBelow(baseTail);

         baseHead.setAbove(head);
         baseTail.setAbove(tail);

         height = 1;

      }

      // clear: Clears the SL so that it's empty.

       public void clear() 
      {
         SkipListSetItem<T> newHead = new SkipListSetItem<>();
         SkipListSetItem<T> newTail = new SkipListSetItem<>();

         newHead.setNext(newTail);
         newTail.setPrev(newHead);

         newHead.setIsHead(true);
         newTail.setIsTail(true);

         head = newHead;
         tail = newTail;

         height = 0;

      }

      public void reBalance()
      {  
         /*ok so for this re-work, I'll do the following:

         1. Get the baseHead and the baseTail
         2. Have them point to the heand and tail, and vice versa.
         3. Set the height back to level 1.
         4. 
         */
         
         if(head.getBelow()==null)return ; //SL is empty

         SkipListSetItem<T> baseHead= baseHead();
         SkipListSetItem<T> baseTail= baseTail();

         reSetList(baseHead,baseTail);

         SkipListSetItem<T> tNode=baseHead;
         SkipListSetItem<T> reNode;
         

         while(!tNode.getNext().getIsTail())
         {
            reNode=tNode.getNext();
            reNode.setAbove(null);
            if(!reNode.getNext().isTail)reNode.getNext().setAbove(null);
            coinFlip(tNode, reNode, reNode.getData());
            tNode=tNode.getNext();

         }
         

      }

   // insertCollection: Inserts a collection of items into the SL

      public void insertCollection(Collection<? extends T> collection) 
      {  

         for (T item : collection) addItem(item);

      }

      public void printList()
      {
         SkipListSetItem<T> tempHead=head;
         SkipListSetItem<T> currentNode;
         int levelCounter=0;

         while(tempHead.getBelow()!=null)tempHead=tempHead.getBelow();

         if(height==1)
         {  
            System.out.println("Height: "+ height);
            currentNode=tempHead.getNext();
            System.out.print("Level "+levelCounter+": ");

            while(!currentNode.getIsTail())
            {  
               System.out.print(currentNode.getData()+" ");
               currentNode=currentNode.getNext();
            }
         }
         else
         {  
            System.out.println("Height: "+ height);
            while(tempHead.getAbove()!=null)
            {
               System.out.print("Level "+levelCounter+": ");
               currentNode=tempHead.getNext();

               while(!currentNode.getIsTail())
               {
                  System.out.print(currentNode.getData()+" ");
                  currentNode=currentNode.getNext();
               }
               
               levelCounter++;
               tempHead=tempHead.getAbove();
               System.out.println();

            }
         }
      }

      public int getHeight()
      {
         return height;
      }

   //getData: access the item wrappers' getter so it can return the data the current node contains. Used in outer class.
   
      public T getData()
      {
         return getData();
      }

   //nodeCount: Counts the number of nodes/items in the SL and returns that count. 

      public int nodeCount()
      {
         SkipListSetItem<T> node = baseHead();
         node=node.getNext();//if the list not empty, will be the first node in the SL,else it will be a tail node.
         int count=0;

         while(!node.isTail)
         {
            count++;
            node=node.getNext();
         }
         return count;
      }

//setHead: Passes a new head to the old head.

      public void setHead(SkipListSetItem<T> newHead)
      {
         head=newHead;
      }

      public SkipListSetItem<T> getHead()
      {
         return head;
      }

// setTail: Passes a new tail to the old tail.

      public void setTail(SkipListSetItem<T> newTail)
      {
         head=newTail;
      }

      public SkipListSetItem<T> getTail()
      {
         return tail;
      }

//isEmpty: Finds the head sentinel node at the base level and check whether the next node is the tail sentinel node
//         if it is, the SL is empty and we return "true."

      public Boolean isEmpty()
      {
         SkipListSetItem<T> node=baseHead().getNext();

         if(node.isTail)return true;

         return false;
      }
        
      @Override
      public boolean hasNext() 
      {
         if(isEmpty())return false;

         return true;   
      }

      @Override
      public void remove() 
      {
        
         SkipListSetItem<T> node= baseHead().getNext();

         if(node.isTail) return ;//SL is empty
         else
         {
            deleteItem(node.getData());
         }

        
      }
      
      //next: will return null if the SL is empty

      @Override
      public T next() 
      { 

         if(hasNext())
         {
            SkipListSetItem<T> node= baseHead().getNext();
            T key=node.getData();
            deleteItem(key);
            return key;
         }
      
         return null;
      }

   }

   SkipListSetIterator<T> skipList;

   public SkipListSet()
   {
      skipList=new SkipListSetIterator<>();
   }

   public SkipListSet(Collection<T> collection)
   {
      skipList=new SkipListSetIterator<>(collection);
   }

   @Override
   public boolean add(T e) 
   {
      //.addItem returns false if item is already in the set, else it returns true after it adds the item.
      return skipList.addItem(e);
   }

   @Override
   public boolean remove(Object o) 
   {  
      if(o instanceof Comparable<?>)//should be satisfied, since Collections only work with primitive data wrapper classes
      {                             //and these wrapper classes extend the Comparable interface.

            @SuppressWarnings("unchecked")
            T key= (T)o; 
            return skipList.deleteItem(key);//false= item not in set and was therefore not removed.
      }

      return false;
   }

   // public void reBalance()
   // {
   //    skipList.reBalance();
   // }

   public void reBalance()
   {
      skipList.reBalance();
   }

   public void printSet()
   {
      skipList.printList();
   }

   @Override
   public int size() 
   {
      return skipList.nodeCount();
   }
   @Override
   public boolean isEmpty() 
   {  
      return skipList.isEmpty();
   }

   //contains method fails if it is given a data type that doesn't match the data type of the SL; else it will return Boolean.

   @Override
   public boolean contains(Object o) 
   {
      if(o instanceof Comparable<?>)
      {
            @SuppressWarnings("unchecked")
            T key= (T)o;
            SkipListSetItem<T> foundNode= skipList.search(key);
            if(foundNode.getIsHead())return false; //this would mean the SL is empty, so we return false.
            else if(foundNode.getData().compareTo(key)==0)return true;
            else return false; 
      }

      return false; //should be unreacheable. 
   }

//iterator:returns instance of populated SkipListSetIterator, which we manipulate with Iterator methods, eg: next().

   @Override
   public Iterator<T> iterator() 
   {
      
      ArrayList<T> collection=skipList.collectItems();

      Iterator<T> test=new SkipListSetIterator<>(collection);

      return test;
   }

   @Override
   public Object[] toArray() 
   {
       //collecItems returns an ArrayList containing all the keys in the skip list, while toArray converts
       //the ArrayList into a primitive array of type Object, which we return.

      Object[] objectArray = skipList.collectItems().toArray();

      return objectArray;
      
   }

   //toArray: must pass a primitive array with the same data type as SL or it will populate the array with
   //         incompatible items.
   
   @Override
   public <T> T[] toArray(T[] a) 
   {  
   //   breakdown of what the method is doing;
   //1. Create primitive array of type Object that holds SL items, using other toArray method.
   //2. Check that "a" has enough space for SL items, if not, handle as seen below.
   //3. Create loop to pass each item in the above array into the generic array "a".
   //4. Due to Java type safety constraints, we cannot directly pass an item from "objArray" to "a" because
   //   the former is of type Object and the latter is generic, which is not allowed in Java.
  //    This means that we need to cast each item in the Object array as a generic item to a generic variable, 
  //    and then we can pass that variable to the primitive generic array as an item. 

      Object[] objArray=toArray();

      if (a.length < objArray.length) 
      {
         @SuppressWarnings("unchecked")
         T[] newArray = (T[])Array.newInstance(a.getClass().getComponentType(), objArray.length);
         a = newArray;
      }

      for(int i=0;i<objArray.length;i++)
      { 
         @SuppressWarnings("unchecked")
         T item=(T)objArray[i]; 
         a[i]=item;
      }

      if(a.length > objArray.length)
      {
         for(int i = objArray.length; i < a.length; i++) a[i] = null;
      }  
      
      return a;
   }

   @Override
   public boolean containsAll(Collection<?> c) 
   {  
      
      if(c.isEmpty()) return false;

      for(Object o:c)if(!contains(o))return false;

      return true;
   }

   @Override
   public boolean addAll(Collection<? extends T> c) 
   {  
      if(c.isEmpty())return false;
      else skipList.insertCollection(c);

      return true;
   }

//retainAll: Breaks if given a collection with different data types, or data types that are not comparable.

   @Override
   public boolean retainAll(Collection<?> c) 
   {  
      //assuming collection is a set of items with the same data type, we check if the items in it are comparable
      //so that we can cast c as a generic collection:

      if(!c.isEmpty() && c.iterator().next() instanceof Comparable<?>)
      {
            @SuppressWarnings("unchecked")
            Collection<T> list= (Collection<T>)c;

            SkipListSetIterator<T> newSkipList= new SkipListSetIterator<>();

            newSkipList.insertCollection(list);
            
            skipList=newSkipList;

            return true;
      }

      return false;
   }

   @Override
   public boolean removeAll(Collection<?> c) 
   {
      if(c.isEmpty()) return false;

      for(Object o:c)
      {
         remove(o);
      }

      if(!isEmpty())return false;

      return true; //true = all items in collection were removed from SL.
   }

   @Override
   public void clear() 
   {
      skipList.clear();
   }

   @Override
   public Comparator<? super T> comparator() 
   {
      return null;//Returns null, as per instructions. 
   }

   //Following three methods throw an UnsupportedOperationException , as per instructions.
   @Override
   public SortedSet<T> subSet(T fromElement, T toElement) 
   {
      throw new UnsupportedOperationException("headSet operation is not supported in SkipListSortedSet.");
   }
   @Override
   public SortedSet<T> headSet(T toElement) 
   {
      throw new UnsupportedOperationException("headSet operation is not supported in SkipListSortedSet.");
   }
   @Override
   public SortedSet<T> tailSet(T fromElement) 
   {
      throw new UnsupportedOperationException("headSet operation is not supported in SkipListSortedSet.");
   }

   @Override
   public T first() 
   {
      SkipListSetItem<T> node=skipList.baseHead().getNext();//gets the first node at the base level; holds the lowest value.

      if(!node.getIsTail())return node.getData();

      else return null; // else, the SL is empty.
      
   }

   @Override
   public T last() 
   {
      SkipListSetItem<T> node=skipList.baseTail().getPrev();

      if(!node.getIsHead())return node.getData();

      else return null;
      
   }

//equals: requires that the item's in object collection are of the same data type as the SL, or it will crash.

   @Override
   public boolean equals(Object o)
   {  
      if(o==this)return true;

      else if(o instanceof Collection<?>)//if it's an instance of collection, we can use the size() method on the object
      {                
         Collection<?> object=(Collection<?>)o;//after casting as Collection and passing to an object that can use method

         if(object.size()==this.size())
         {
            return containsAll(object);
         }
         else return false;
      }
      
      return false;
   }

   @Override
   public int hashCode() 
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((skipList == null) ? 0 : skipList.hashCode());
      return result;
   }

   public Integer objectNodeCount(Object o)
   {
      if(o instanceof SkipListSet)
      {  
         @SuppressWarnings("unchecked")//We supress the warning because we have two condtions at this point for type safety.
         SkipListSet<T> object=(SkipListSet<T>)o;

        return object.size();

      }

      return Integer.MIN_VALUE; // it's not a SkipListSet, therefore return negative infinity, but it shouldn't reach this.

   }
   
}