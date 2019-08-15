from scipy.spatial import ConvexHull
import numpy as np
from mayavi.mlab import *
import random
import math
import time

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

def constructConvexHullsBruteForce():
    for firstPoint in np.arange(numberOfPoints):
        pointIds = [firstPoint]
        constructChonvexHullsHelper(pointIds)

def constructConvexHullsBruteForcePruning():
    for firstPoint in np.arange(numberOfPoints):
        pointIds = [firstPoint]
        constructChonvexHullsHelperPruning(pointIds)

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
    #construct 'child' hulls
    for i in np.arange(pointIds[-1]+1,numberOfPoints):
        newPointIds = list(pointIds)
        newPointIds.append(i)
        constructChonvexHullsHelper(newPointIds)

def constructChonvexHullsHelperPruning(pointIds):
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
                    # pruning
                    return
            else:
                # pruning
                return
    #construct 'child' hulls
    for i in np.arange(pointIds[-1]+1,numberOfPoints):
        newPointIds = list(pointIds)
        newPointIds.append(i)
        constructChonvexHullsHelperPruning(newPointIds)

@animate(delay = 5000)
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
        if(i%500==0):
            print("i: ", i)
        neighbourPointIds = generateNeighbour(currentSolution,3,3)
        hull = constructConvexHullFromIds(neighbourPointIds)
        if hull != None:
            if len(hull.points)==len(hull.vertices):
                mesh = Mesh(hull,neighbourPointIds)
                if mesh.isEmpty() and mesh.hull.volume>bestVolume:
                    meshes.append(mesh)
                    #track maximum hull
                    global maxVolume
                    global maxMesh
                    maxVolume = mesh.hull.volume
                    bestVolume = mesh.hull.volume
                    maxMesh = mesh
                    currentSolution = mesh.ids
                    print(mesh.ids, mesh.hull.volume)

def simulatedAnnealing1(initSolution, iterations,startTemperature,endTemperature):
    y = math.log10(endTemperature/startTemperature)/iterations
    beta = math.pow(10, y)
    #print("Calculated beta: ", beta)
    currentSolution = initSolution.ids
    currentVolume = initSolution.hull.volume
    meshes.append(initSolution)
    temperature = startTemperature
    for i in np.arange(iterations):
        temperature = temperature*beta
        if(i%1000==0):
            print("i: ", i)
        neighbourPointIds = generateNeighbour(currentSolution,3,3)
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
                            #print(hull.volume, mesh.ids)
                    else:
                        volumeDifference = currentVolume-hull.volume
                        if volumeDifference==0:
                            continue
                        p = math.exp(-(volumeDifference/temperature))
                        if p > np.random.random_sample():
                            meshes.append(mesh)
                            currentVolume = hull.volume
                            currentSolution = mesh.ids
                            #print(hull.volume, mesh.ids, volumeDifference, temperature, p)


def simulatedAnnealing2(initSolution, iterations,startTemperature,endTemperature):
    y = math.log10(endTemperature/startTemperature)/iterations
    beta = math.pow(10, y)
    #print("Calculated beta: ", beta)
    currentSolution = initSolution.ids
    currentVolume = initSolution.hull.volume
    meshes.append(initSolution)
    temperature = startTemperature
    for i in np.arange(iterations):
        temperature = temperature*beta
        if(i%500==0):
            print("i: ", i)
        mesh = generateNeighbour2(currentSolution,3,3)
        if mesh.hull.volume>currentVolume:
            meshes.append(mesh)
            currentVolume = mesh.hull.volume
            currentSolution = mesh.ids
            #track maximum hull
            global maxVolume
            global maxMesh
            if currentVolume>maxVolume:
                maxMesh = mesh
                maxVolume = mesh.hull.volume
                print(mesh.hull.volume, mesh.ids)
            else:
                print(mesh.hull.volume, mesh.ids, temperature)

        else:
            volumeDifference = currentVolume-mesh.hull.volume
            if volumeDifference==0:
                continue
            p = math.exp(-(volumeDifference/temperature))
            if p > np.random.random_sample():
                meshes.append(mesh)
                currentVolume = mesh.hull.volume
                currentSolution = mesh.ids
                print(mesh.hull.volume, mesh.ids, volumeDifference, temperature, p)

def generateNeighbour(pointIds, maxAdd, maxRemove):
    #always min 4 points needed
    #ADD POINTS
    newPointIds = list(pointIds)
    numberOfPointsToAdd = random.randint(0, maxAdd)
    possiblePointsToAdd = [x for x in list(range(numberOfPoints)) if x not in pointIds]
    random.shuffle(possiblePointsToAdd)
    random.shuffle(newPointIds)
    for i in np.arange(numberOfPointsToAdd):
        if i>len(possiblePointsToAdd)-1:
            break
        newPointIds.append(possiblePointsToAdd[i])

    #delete points
    numberOfPointsToRemove = random.randint(0, min(len(newPointIds)-4,maxRemove))
    for i in np.arange(numberOfPointsToRemove):
        newPointIds.pop(0)
    return newPointIds

def generateNeighbour2(pointIds, maxAdd, maxRemove):
    bestVolumeNeighbour = 0
    feasibleNeighbours = 0
    while feasibleNeighbours!=2:
        #always min 4 points needed
        #ADD POINTS
        newPointIds = list(pointIds)
        numberOfPointsToAdd = random.randint(0, maxAdd)
        possiblePointsToAdd = [x for x in list(range(numberOfPoints)) if x not in pointIds]
        random.shuffle(possiblePointsToAdd)
        random.shuffle(newPointIds)
        for i in np.arange(numberOfPointsToAdd):
            if i>len(possiblePointsToAdd)-1:
                break
            newPointIds.append(possiblePointsToAdd[i])

        #delete points
        numberOfPointsToRemove = random.randint(0, min(len(newPointIds)-4,maxRemove))
        for i in np.arange(numberOfPointsToRemove):
            newPointIds.pop(0)
        hull = constructConvexHullFromIds(newPointIds)
        if hull != None:
            if len(hull.points)==len(hull.vertices):
                mesh = Mesh(hull,newPointIds)
                if mesh.isEmpty():
                    feasibleNeighbours+=1
                    if mesh.hull.volume > bestVolumeNeighbour:
                        bestVolumeNeighbour = mesh.hull.volume
                        bestMesh = mesh
    return bestMesh

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
            #print("Initial solution found after ", i, " iterations: ", mesh.ids)
            return mesh
        i+=1

def initVariables():
    global meshes
    global maxVolume
    global maxMesh
    meshes=[]
    maxVolume = 0
    maxMesh = None

def generateInputPointsConfig0():
    l = []
    point = np.asarray((5,5,5))
    while len(l) != numberOfPoints:
        newPoint = (10*np.random.random_sample(), 10*np.random.random_sample(), 10*np.random.random_sample())
        dist = np.linalg.norm(point-np.asarray(newPoint))
        if(dist>4):
            l.append(newPoint)
    return l

def generateInputPointsConfig1():
    return [(10*np.random.random_sample(), 10*np.random.random_sample(), 10*np.random.random_sample()) for i in range(numberOfPoints)]


f= open("output.txt","w+")



comparisonList = []
initVariables()

# [xCoordinates,yCoordinates,zCoordinates] = list(zip(*inputPoints))
# print("Start brute force with pruning for ", numberOfPoints, " points")
# begin = int(round(time.time() * 1000))
# constructConvexHullsBruteForcePruning() #adds constructed hulls to meshes list, finds maxMesh and maxVolume
# end = int(round(time.time() * 1000))
# comparisonList.append(maxMesh)
# print("Time elapsed: ", end-begin)
# print("Maximum mesh:", maxMesh.ids, maxMesh.hull.volume)


#random seed for repeatibity
np.random.seed(1)


for j in np.asarray([60]):
    numberOfPoints = j
    toFile = []
    print("--------------------------------------------")
    print("Running for ", numberOfPoints)
    toFile.append(numberOfPoints)

    for i in np.arange(2):
        #generate points
        if i==0:
            inputPoints = generateInputPointsConfig0()
        else:
            inputPoints = generateInputPointsConfig1()

        [xCoordinates,yCoordinates,zCoordinates] = list(zip(*inputPoints))

        tavlocal = 0
        tavsimulated = 0
        initSolution = searchInitSolution()
        initVariables()
        print("Start local search")
        begin = int(round(time.time() * 1000))
        localSearch(initSolution,5000)
        end = int(round(time.time() * 1000))
        tlocal = end - begin
        comparisonList.append(maxMesh)
        aantalPuntenHC = len(maxMesh.hull.vertices)
        print("Maximum mesh local search:", maxMesh.hull.volume)
        toFile.append(maxMesh.hull.volume)
        initVariables()
        print("Start simulated annealing")
        begin = int(round(time.time() * 1000))
        # simulatedAnnealing1(initSolution, 5000, 10, 0.2)
        end = int(round(time.time() * 1000))
        tsimulated = end - begin
        comparisonList=meshes
        aantalPuntenSA = len(maxMesh.hull.vertices)
        print("Maximum mesh simulated annealing:",maxMesh.hull.volume)
        toFile.append(maxMesh.hull.volume)
        tavlocal += tlocal
        tavsimulated += tsimulated
        tavlocal = tavlocal/1
        tavsimulated = tavsimulated/1
        toFile.append("HC: aantal punten", str(aantalPuntenHC))
        toFile.append("SA: aantal punten", str(aantalPuntenSA))
        f.write(str(toFile))
        f.write("\n")

# meshes.sort(key=lambda x: x.hull.volume,reverse=True) #sort by decreasing volume
#
# plot points
# points3d(xCoordinates,yCoordinates,zCoordinates,scale_factor=0.2,color=(1,0,0))
# # show axes
# axes(nb_labels=6,extent=[0,10,0,10,0,10])
# # plot point ids
# for i in range(numberOfPoints):
#    text3d(xCoordinates[i],yCoordinates[i],zCoordinates[i],str(i),scale=(0.5, 0.5, 0.5))
#
# anim(comparisonList)
#
# show()
f.close()
