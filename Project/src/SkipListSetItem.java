
public class SkipListSetItem <T> 
{
  
   private SkipListSetItem<T>  above;
   private SkipListSetItem<T>  below;
   private SkipListSetItem<T>  prev;
   private SkipListSetItem<T>  next;
   
   private T data;//also referred to as a key

   Boolean isHead;
   Boolean isTail;

//Constructor:

public SkipListSetItem(T data)
{
   above=null;
   below=null;
   next=null;
   prev=null;

   this.data=data;

   isHead=false;
   isTail=false;

}
//We use this constructor for the sentinel SkipListSetItems, so that they remain generic.
public SkipListSetItem()
{
   above=null;
   below=null;
   next=null;
   prev=null;

   isHead=false;
   isTail=false;

}

//Setters and getters:

   public SkipListSetItem<T> getAbove() 
   {
      return above;
   }

   public void setAbove(SkipListSetItem<T> above) 
   {
      this.above = above;
   }

   public SkipListSetItem<T> getBelow() {
      return below;
   }

   public void setBelow(SkipListSetItem<T> below) {
      this.below = below;
   }

   public SkipListSetItem<T> getNext() {
      return next;
   }

   public void setNext(SkipListSetItem<T> next) {
      this.next = next;
   }

   public T getData() {
      return data;
   }

   public void setData(T data) {
      this.data = data;
   }

   public SkipListSetItem<T> getPrev() {
      return prev;
   }


   public void setPrev(SkipListSetItem<T> prev) {
      this.prev = prev;
   }


   public Boolean getIsHead() {
      return isHead;
   }


   public void setIsHead(Boolean isHead) {
      this.isHead = isHead;
   }


   public Boolean getIsTail() {
      return isTail;
   }


   public void setIsTail(Boolean isTail) {
      this.isTail = isTail;
   }

   

}
