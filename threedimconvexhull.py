from scipy.spatial import ConvexHull
import numpy as np
from mayavi.mlab import *
import random
import math

def constructConvexHullFromIds(givenPointIds):
    givenPoints = []
    for id in givenPointIds:
        givenPoints.append(inputPoints[id])
    try:
        return ConvexHull(givenPoints)
    except:
        return None

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
        surface = triangular_mesh(x, y, z, self.hull.simplices,color=(0,0,1),opacity=0.5)
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
        rayOrigin = inputPoints[indexPoint]
        rayVector = (0,0,50)
        numberOfIntersections = 0
        for triangle in self.hull.simplices:
            index1 = self.ids[triangle[0]]
            index2 = self.ids[triangle[1]]
            index3 = self.ids[triangle[2]]
            p1 = inputPoints[index1]
            p2 = inputPoints[index2]
            p3 = inputPoints[index3]
            if isIntersection(p1, p2, p3, rayOrigin, rayVector):
                numberOfIntersections+=1
        if numberOfIntersections == 1:
            return True
        return False

def isIntersection(p1,p2,p3,rayOrigin,rayVector):
    #moller-trumbore algorithm
    epsillon = 0.0000000000000001
    edge1 = np.subtract(p2,p1)
    edge2 = np.subtract(p3,p1)
    h = np.cross(rayVector,edge2)
    a = np.dot(edge1,h)

    if a<epsillon and a>-epsillon:
        return False

    f = 1.0 / a
    s = np.subtract(rayOrigin,p1)
    u = f * (np.dot(s,h));
    if u < 0.0 or u > 1.0:
        return False
    q = np.cross(s, edge1)
    v = f * np.dot(rayVector, q)
    if v < 0.0 or (u + v > 1.0):
        return False
    t = f * np.dot(edge2,q);
    if t > epsillon:
        return True
    else:
        return False

def constructConvexHulls():
    for firstPoint in np.arange(numberOfPoints):
        pointIds = [firstPoint]
        constructChonvexHullsHelper(pointIds)

def constructChonvexHullsHelper(pointIds):
    if len(pointIds)>3:
        # print(pointIds)
        #calculate convex hull
        hull = constructConvexHullFromIds(pointIds)
        if hull != None:
            # print(pointIds)
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
def anim(meshes):
    (surface, edges) = meshes[0].plot()
    while True:
        for i in np.arange(len(meshes)):
            mesh = meshes[i]
            print('Updating scene: ',i, 'with volume: ', mesh.hull.volume)
            mesh.printFaces()
            [x,y,z] = list(zip(*mesh.hull.points))
            surface.mlab_source.reset(x=x,y=y,z=z,triangles = mesh.hull.simplices)
            edges.mlab_source.reset(x=x,y=y,z=z,triangles = mesh.hull.simplices)
            yield

def localSearch(initSolution, iterations):
    currentSolution = initSolution.ids
    bestVolume = initSolution.hull.volume
    meshes.append(initSolution)
    for i in np.arange(iterations):
        if(i%100==0):
            print("i: ", i)
        neighbourPointIds = generateNeighbour(currentSolution)
        hull = constructConvexHullFromIds(neighbourPointIds)
        if hull != None:
            if len(hull.points)==len(hull.vertices):
                mesh = Mesh(hull,neighbourPointIds)
                if mesh.isEmpty() and mesh.hull.volume>bestVolume:
                    meshes.append(mesh)
                    #track maximum hull
                    global maxVolume
                    global maxMesh
                    maxVolume = hull.volume
                    bestVolume = hull.volume
                    maxMesh = mesh
                    currentSolution = mesh.ids
                    print(mesh.ids, mesh.hull.volume)

def simulatedAnnealing(initSolution, iterations,startTemperature,beta):
    currentSolution = initSolution.ids
    currentVolume = initSolution.hull.volume
    meshes.append(initSolution)
    temperature = startTemperature
    for i in np.arange(iterations):
        temperature = temperature*beta
        if(i%100==0):
            print("i: ", i)
        neighbourPointIds = generateNeighbour(currentSolution)
        hull = constructConvexHullFromIds(neighbourPointIds)
        if hull != None:
            if len(hull.points)==len(hull.vertices):
                mesh = Mesh(hull,neighbourPointIds)
                if mesh.isEmpty():
                    if hull.volume>currentVolume:
                        meshes.append(mesh)
                        currentVolume = hull.volume
                        currentSolution = mesh.ids
                        #track maximum hull
                        global maxVolume
                        global maxMesh
                        if currentVolume>maxVolume:
                            maxMesh = mesh
                            maxVolume = hull.volume
                            print(hull.volume, mesh.ids)
                    else:
                        volumeDifference = currentVolume-hull.volume
                        if volumeDifference==0:
                            continue
                        p = math.exp(-(volumeDifference/temperature))
                        if p > np.random.random_sample():
                            meshes.append(mesh)
                            currentVolume = hull.volume
                            currentSolution = mesh.ids

def generateNeighbour(pointIds):
    #always min 4 points needed
    #ADD POINTS
    newPointIds = list(pointIds)
    numberOfPointsToAdd = random.randint(0, 3)
    possiblePointsToAdd = [x for x in list(range(numberOfPoints)) if x not in pointIds]
    random.shuffle(possiblePointsToAdd)
    random.shuffle(newPointIds)
    for i in np.arange(numberOfPointsToAdd):
        if i>len(possiblePointsToAdd)-1:
            break
        newPointIds.append(possiblePointsToAdd[i])

    #delete points
    numberOfPointsToRemove = random.randint(0, min(len(newPointIds)-4,3))
    for i in np.arange(numberOfPointsToRemove):
        newPointIds.pop(0)
    return newPointIds

def searchInitSolution():
    initSolutionFound = False
    i=0
    while True:
        ids = []
        while len(ids)!=4:
            randomId = np.random.randint(0,numberOfPoints)
            if randomId not in ids:
                ids.append(randomId)
        hull = constructConvexHullFromIds(ids)
        mesh = Mesh(hull, ids)
        if mesh.isEmpty():
            print("Initial solution found after ", i, " iterations: ", mesh.ids)
            return mesh
        i+=1

#random seed for repeatibity
# np.random.seed(5)

#generate points
numberOfPoints = 500
inputPoints = [(10*np.random.random_sample(), 10*np.random.random_sample(), 10*np.random.random_sample()) for i in range(numberOfPoints)]
[xCoordinates,yCoordinates,zCoordinates] = list(zip(*inputPoints))
print(inputPoints)


comparisonList = []
meshes = []
maxVolume = 0
maxMesh = None

initSolution = searchInitSolution()


localSearch(initSolution,5000)
comparisonList.append(maxMesh)
print(maxMesh.ids, maxMesh.hull.volume)
print(len(meshes))

meshes=[]
maxVolume = 0
maxMesh = None

simulatedAnnealing(initSolution,5000,50,0.999)
comparisonList.append(maxMesh)
print(maxMesh.ids, maxMesh.hull.volume)
print(len(meshes))

# meshes=[]
# maxVolume = 0
# maxMesh = None
# constructConvexHulls() #adds constructed hulls to meshes list, finds maxMesh and maxVolume
# print(maxMesh.ids, maxMesh.hull.volume)
# print(len(meshes))

#meshes.sort(key=lambda x: x.hull.volume,reverse=True) #sort by decreasing volume


#plot points
points3d(xCoordinates,yCoordinates,zCoordinates,scale_factor=0.2,color=(1,0,0))
#show axes
axes(nb_labels=6,extent=[0,10,0,10,0,10])
#plot point ids
for i in range(numberOfPoints):
    text3d(xCoordinates[i],yCoordinates[i],zCoordinates[i],str(i),scale=(0.5, 0.5, 0.5))

anim(comparisonList)

show()
