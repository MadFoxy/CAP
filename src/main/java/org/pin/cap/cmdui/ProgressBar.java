package org.pin.cap.cmdui;

public class ProgressBar {

  private double _lastIncrement = 0;
  public double _currentTick = 0;
  private double _maxTicks = 0;
  private Type _type = null;
  private int _length = 100;
  private String _progressMsg;


  public ProgressBar( int length, float maxTicks, Type type ) {
    _maxTicks = maxTicks;
    _length = length;
    _type = type;
  }

  public synchronized void tick( Double increment,String progressMsg ) {
    _lastIncrement = increment;
    _currentTick += increment;
    _progressMsg = progressMsg;
    update();
  }

  public void complete() {
    _currentTick = _maxTicks;
    update();
  }

  public void update() {
      //if( _maxTicks < ( _currentTick + _lastIncrement ) )
      //  _currentTick = _maxTicks;
      int current = (int)((_currentTick/_maxTicks) * _length );
      StringBuilder statusData = new StringBuilder( "(" );
      statusData.append( (int)_currentTick + " of " + (int)_maxTicks);
      statusData.append( ")" );
      StringBuilder progressBar = new StringBuilder( "[" );
      for(float x=0; x<current; x++ ){
        progressBar.append("=");
      }
      progressBar.append(">");
      for(float x=current; x<_length; x++ ){
        progressBar.append(".");
      }
      progressBar.append("]");

        //+" "+_progressMsg
      if( _type == Type.BAR ){
        System.out.print( "\r" + "Processing: " + progressBar.toString() );
      }
      else if( _type == Type.DATA ){
        System.out.print("\r" + "Processing: " + statusData.toString());
      }
      else{
        System.out.print( "\r" +  "Processing: " + progressBar.toString() + " " + statusData.toString());
      }
      if(current>=_length){
          System.out.println(" "+_progressMsg);
      }

  }

//  public static void main( String... args ) throws Throwable {
//
//
//    int length = Integer.parseInt( args[0] ), max = Integer.parseInt( args[1] ),
//        increment = Integer.parseInt( args[2] ), wait = Integer.parseInt( args[3] );
//    ProgressBar bar = new ProgressBar( length, (float)max, Type.BOTH );
//    for( int x=0; x<max; x+=increment ) {
//    //  System.out.println("\r" + x);
//      bar.tick((float) increment,"xxx"+x);
//      Thread.sleep(wait);
//
//
//    }
//    System.out.println();
//  }
}