import numpy as np
import pyrender
import trimesh
import trimesh.visual
import matplotlib.pyplot as plt

RADIAN = np.pi / 180


class OnlineVisualizer:
    def __init__(self, resolution=(512, 512)):
        self.surfaces = None
        plt.rcParams["figure.figsize"] = [8, 8]
        camera = pyrender.PerspectiveCamera(yfov=np.pi / 3.0)
        light = pyrender.DirectionalLight(color=[255, 255, 255], intensity=5)
        self.scene = pyrender.Scene(ambient_light=[.1, .1, .1], bg_color=[1., 1., 1.])
        light_rotation_1 = np.array([[np.cos(45 * RADIAN), np.sin(45 * RADIAN), 0, 0],
                                     [-np.sin(45 * RADIAN), np.cos(45 * RADIAN), 0, 0],
                                     [0, 0, 1, 0],
                                     [0, 0, 0, 1]])
        light_rotation_2 = np.array([[np.cos(30 * RADIAN), 0, -np.sin(30 * RADIAN), 0],
                                     [0, 1, 0, 0],
                                     [np.sin(30 * RADIAN), 0, np.cos(30 * RADIAN), 0],
                                     [0, 0, 0, 1]])
        light_position = np.dot(light_rotation_1, light_rotation_2)
        self.scene.add(light, pose=light_position)
        self.scene.add(camera, pose=[[1, 0, 0, 0],
                                     [0, np.cos(0 * RADIAN), -np.sin(0 * RADIAN), 0],
                                     [0, np.sin(0 * RADIAN), np.cos(0 * RADIAN), 0.2],
                                     [0, 0, 0, 1]])
        self.object_pose = [[1, 0, 0, 0],
                            [0, np.cos(35 * RADIAN), -np.sin(35 * RADIAN), 0],
                            [0, np.sin(35 * RADIAN), np.cos(35 * RADIAN), 0],
                            [0, 0, 0, 1]]
        self.r = pyrender.OffscreenRenderer(*resolution)
        start_image, _ = self.r.render(self.scene)
        plt.suptitle('', fontsize=16)
        axes = plt.subplot(111)
        self.image = axes.imshow(start_image)
        plt.ion()

    def set_surfaces(self, surfaces):
        self.surfaces = surfaces

    def render(self, vertices, pause=None):
        m = trimesh.Trimesh(vertices=vertices, faces=self.surfaces, face_colors=np.array([[0.5, 0, 0],
                                                                                          [0.5, 0, 0],
                                                                                          [0, 0.5, 0],
                                                                                          [0, 0, 0.5],
                                                                                          [0, 0, 0.5],
                                                                                          [0, 0.5, 0],
                                                                                          [0.5, 0, 0],
                                                                                          [0.5, 0, 0],
                                                                                          [0, 0.5, 0],
                                                                                          [0, 0, 0.5],
                                                                                          [0, 0, 0.5],
                                                                                          [0, 0.5, 0]]))
        mesh = pyrender.Mesh.from_trimesh(m, smooth=False)
        obj = self.scene.add(mesh, pose=self.object_pose)
        color, _ = self.r.render(self.scene)
        self.image.set_data(color)
        if pause is None:
            plt.waitforbuttonpress()
        else:
            plt.pause(pause)
        self.scene.remove_node(obj)


def cube_rotation():
    side_size = 0.025
    vertices = np.array([
        [-side_size, -side_size, -side_size],
        [-side_size, -side_size, side_size],
        [side_size, -side_size, -side_size],
        [side_size, -side_size, side_size],
        [-side_size, side_size, -side_size],
        [-side_size, side_size, side_size],
        [side_size, side_size, -side_size],
        [side_size, side_size, side_size],
    ])
    faces = np.array([
        [2, 3, 1, 0],
        [4, 5, 7, 6],
        [1, 3, 7, 5],
        [6, 7, 3, 2],
        [5, 4, 0, 1],
        [4, 6, 2, 0]
    ])
    rotation_x = np.array([[np.cos(3 * RADIAN), 0, -np.sin(3 * RADIAN)],
                           [0, 1, 0],
                           [np.sin(3 * RADIAN), 0, np.cos(3 * RADIAN)]])
    v = OnlineVisualizer()
    v.set_surfaces(faces)
    while True:
        v.render(vertices, pause=0.001)
        vertices = vertices @ rotation_x


if __name__ == '__main__':
    cube_rotation()
