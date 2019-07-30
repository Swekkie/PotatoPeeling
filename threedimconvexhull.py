from scipy.spatial import ConvexHull
import numpy as np
from mayavi.mlab import *

def isIntersection(p1,p2,p3,pA,pB):
    a1 = signedVolume(pA,p1,p2,p3)
    a2 = signedVolume(pB,p1,p2,p3)
    a3 = signedVolume(pA,pB,p1,p2)
    a4 = signedVolume(pA,pB,p2,p3)
    a5 = signedVolume(pA,pB,p3,p1)
    if np.sign(a1)!=np.sign(a2) and np.sign(a3)==np.sign(a4) and  np.sign(a4)==np.sign(a5):
        return True
    else:
        return False

def signedVolume(a,b,c,d):
    e1 = np.subtract(b,a)
    e2 = np.subtract(c,a)
    e3 = np.subtract(d,a)
    cross = np.cross(e1,e2)
    dot = np.dot(cross,e3)
    return dot

def constructConvexHullFromIds(givenPointIds):
    givenPoints = []
    for id in givenPointIds:
        givenPoints.append(inputPoints[id])
    return ConvexHull(givenPoints)

class Mesh:
    def __init__(self, hull, ids):
        self.hull = hull
        self.ids = ids

    def printFaces(self):
        for face in self.hull.simplices:
            print([self.ids[face[0]],self.ids[face[1]],self.ids[face[2]]])

    def plot(self):
        [x,y,z] = list(zip(*self.hull.points))
        #plot surface mesh
        surface = triangular_mesh(x, y, z, self.hull.simplices,color=(0,0,1),opacity=0.75)
        #plot edges mesh
        edges = triangular_mesh(x, y, z, self.hull.simplices,representation='wireframe',color=(0,0,0))
        return (surface, edges)

    def isEmpty(self):
        for i in range(numberOfPoints):
            if i not in self.ids:
                if self.isInsideMesh(i):
                    return False
        return True

    def isInsideMesh(self,indexPoint):
        point = inputPoints[indexPoint]
        pA = np.asarray(point)
        pA[2] = 10
        pB = np.asarray(point)
        pB[2] = -10
        numberOfIntersections = 0
        for triangle in self.hull.simplices:
            index1 = self.ids[triangle[0]]
            index2 = self.ids[triangle[1]]
            index3 = self.ids[triangle[2]]
            p1 = inputPoints[index1]
            p2 = inputPoints[index2]
            p3 = inputPoints[index3]
            if isIntersection(p1, p2, p3, pA, pB):
                numberOfIntersections+=1
        if numberOfIntersections == 0:
            return False
        return True

def constructConvexHulls():
    for firstPoint in np.arange(numberOfPoints):
        pointIds = [firstPoint]
        constructChonvexHullsHelper(pointIds)

def constructChonvexHullsHelper(pointIds):
    if len(pointIds)>3:
        #calculate convex hull
        hull = constructConvexHullFromIds(pointIds)
        if len(hull.points)==len(hull.vertices):
            mesh = Mesh(hull,pointIds)
            if mesh.isEmpty():
                meshes.append(mesh)
                #track maximum hull
                global maxVolume
                global maxMesh
                if hull.volume > maxVolume:
                    maxVolume = hull.volume
                    maxMesh = mesh
            else:
                return
        else:
            return
    #construct 'child' hulls
    for i in np.arange(pointIds[-1]+1,numberOfPoints):
        newPointIds = list(pointIds)
        newPointIds.append(i)
        constructChonvexHullsHelper(newPointIds)

@animate(delay = 1000)
def anim():
    while True:
        for i in np.arange(len(meshes)):
            mesh = meshes[i]
            print('Updating scene: ',i, 'with volume: ', mesh.hull.volume)
            [x,y,z] = list(zip(*mesh.hull.points))
            surface.mlab_source.reset(x=x,y=y,z=z,triangles = mesh.hull.simplices)
            edges.mlab_source.reset(x=x,y=y,z=z,triangles = mesh.hull.simplices)
            yield

#random seed for repeatiblity
np.random.seed(25)

#generate points
numberOfPoints = 10
inputPoints = [(np.random.random_sample(), np.random.random_sample(), np.random.random_sample()) for i in range(numberOfPoints)]
[xCoordinates,yCoordinates,zCoordinates] = list(zip(*inputPoints))
print(inputPoints)

meshes = []
maxVolume = 0
maxMesh = None
constructConvexHulls() #adds constructed hulls to meshes list
meshes.sort(key=lambda x: x.hull.volume,reverse=True) #sort by decreasing volume

print(len(meshes))
print(maxVolume)
print(meshes[0].hull.volume)
#plot points
points3d(xCoordinates,yCoordinates,zCoordinates,scale_factor=0.02,color=(1,0,0))
#show axes
axes(nb_labels=6,ranges=[0,1,0,1,0,1])
#plot point ids
for i in range(numberOfPoints):
    text3d(xCoordinates[i],yCoordinates[i],zCoordinates[i],str(i),scale=(0.05, 0.05, 0.05))

(surface,edges) = maxMesh.plot()


anim()
show()
