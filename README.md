# fxdxftoolkit
DXF Parser that projects to 2D for JavaFX Scene Graph

This is a long-overdue publication of a JavaFX conversion of a Graphics2D extrapolation of a long-defunct graduate school project by a Spanish technical student (unreachable for years even when using a Spanish co-worker to try to track him down) which was originally written for the old discontinued Java3D toolkit.

There are far better toolkits than this one, but they aren't open source, can't be extended, and are very expensive. The best one is 10-40 times more performant than any other toolkit when I last evaluated it in 2019 and is based on IBM's highly performant SWT canvas. There was anothert fast-growing commercial API at the time that I seem to recall was JavaFX based.

This can be a good toolkit for those who don't have the budget for a commercial library, only need DXF support and not DWG support, have no need for rich text (I dropped some entity support during the JavaFX transfer from Graphics2D as I discovered some of those more complex Dimension-based entities were even incorrect and incomplete in the original Java3D version), and just want something for a quick vector graphics overlay without editing features.
