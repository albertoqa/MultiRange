# MultiRange Control

MultiRange is a JavaFX control similar to the default Slider control but with support for any number of thumbs (always in pairs).

## Usage

![alt tag](./multirange.gif)

To create a new range just click over the slider or over any range. A range can be modified dragging its thumbs.

To delete a range right-click over the range.

### Create programatically

```Java
// Create a new MultiRange with min/max values
MultiRange multiRange = new MultiRange(0, 100);

// Show tick marks and tick labels
multiRange.setShowTickLabels(true);
multiRange.setShowTickMarks(true);

// Set major tick unit and minor tick count
multiRange.setMajorTickUnit(2);
multiRange.setMinorTickCount(10);

// Make it snap to ticks
multiRange.setSnapToTicks(true);

```

### Create with FXML

```xml
<?import multirange.MultiRange?>
...
<AnchorPane>
   <children>
      <MultiRange fx:id="multiRange" stylesheets="@./multirange.css"/>
   </children>
</AnchorPane>

```

## Installation

### Maven

```xml
<dependency>
  <groupId>com.albertoquesada</groupId>
  <artifactId>multi-range</artifactId>
  <version>0.1</version>
</dependency>
```

### Manual

[Download](https://oss.sonatype.org/content/groups/public/com/albertoquesada/multi-range/0.1/multi-range-0.1.jar) the JAR file and place it on your classpath.