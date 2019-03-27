import matplotlib.pyplot as plt
import numpy as np
from matplotlib.patches import Polygon
import matplotlib.animation as animation

fig, ax = plt.subplots()

path1 = "D:/Masterproef/PotatoPeeling/pointset.txt"
path2 = "D:/Masterproef/PotatoPeeling/foundpolygons.txt"

with open(path1,"r") as f:
    points = f.read().splitlines()
with open(path2,"r") as f:
    foundpolygons = f.read().splitlines()

x = []
y = []

for point in points:
	xyvalues = point.split(";")
	x.append(float(xyvalues[0]))
	y.append(float(xyvalues[1][:-1]))
	print(xyvalues[0] +"		"+ xyvalues[1][:-1])


ax.scatter(x,y)
axes = fig.gca();
axes.set_xlim([0,40])
axes.set_ylim([0,40])

#add labels
for i in range(0,len(x)) :
    ax.annotate(
        i+1,
        xy=(x[i], y[i]), xytext=(0, 5),
        textcoords='offset points')

polygonList = []
for polygon in foundpolygons:
    xCoordinates = []
    yCoordinates = []
    ids = polygon.strip().split(" ")
    ids = list(map(int, ids))
    numberOfPoints = len(ids)
    npArray = np.empty((numberOfPoints,2))

    for i in range(0,numberOfPoints):
        id = ids[i]
        npArray[i,0]= x[id-1]
        npArray[i,1]= y[id-1]

    p = Polygon(npArray,True,visible=False, color = 'r')
    polygonList.append(p)
    ax.add_patch(p);

numberOfPolygons = len(polygonList)
print(len(polygonList))

def switchBetweenPolygons(num, polygonList):
    previous = num - 1
    if previous < 0:
        previous = len(polygonList)-1
    polygonList[num].set_visible(True)
    polygonList[previous].set_visible(False)

    return polygonList

ani = animation.FuncAnimation(fig, switchBetweenPolygons, numberOfPolygons, fargs=(polygonList,), interval=500, blit = False)
#comment above line and uncomment line below to show biggest polygon only
#polygonList[0].set_visible(True)


plt.show()
