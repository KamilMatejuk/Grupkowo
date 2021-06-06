# external packages
from pydantic import BaseModel
from typing import Optional, List
from enum import Enum

##############################################################
########################## objects ###########################
##############################################################

class Group(BaseModel):
    group_id: int
    name: str
    image: Optional[str] = None

class User(BaseModel):
    user_id: int
    username: str
    avatar: Optional[bytes] = None

class Post(BaseModel):
    post_id: int
    created: int
    text: str
    author_id: int
    author_username: str
    likes: int
    author_liked: bool
    author_avatar: Optional[bytes] = None

class Comment(BaseModel):
    comment_id: int
    created: int
    text: str
    author_id: int
    author_username: str

class Message(BaseModel):
    message_id: int
    created: int
    text: str
    author_id: int
    author_username: str
    likes: int
    author_liked: bool

##############################################################
####################### request models #######################
##############################################################

class RequestLogin(BaseModel):
    name: str
    password: str
    
class RequestRegister(BaseModel):
    username: str
    email: str
    password: str
    
class RequestEditUser(BaseModel):
    username: Optional[str] = None
    email: Optional[str] = None
    password: Optional[str] = None
    avatar: Optional[str] = None

class RequestCreateGroup(BaseModel):
    name: str
    image: Optional[bytes] = None

class RequestAddUser(BaseModel):
    user_id: int

class RequestCreatePost(BaseModel):
    text: str

class RequestCreateComment(BaseModel):
    text: str

class RequestCreateMessage(BaseModel):
    text: str
    
##############################################################
####################### response models ######################
##############################################################

class ResponseToken(BaseModel):
    access_token: str
    token_type: str

class ResponseUserGroups(BaseModel):
    groups: List[Group]

class ResponseUserProfile(BaseModel):
    user_id: int
    username: str
    email: str
    avatar: Optional[bytes] = None

class ResponseAllUsers(BaseModel):
    users: List[User]

class ResponseCollegueProfile(BaseModel):
    user_id: int
    username: str
    avatar: Optional[bytes] = None

class ResponseGroup(BaseModel):
    name: str
    admin_id: int
    image: Optional[bytes] = None

class ResponseGroupUsers(BaseModel):
    users: List[User]

class ResponsePosts(BaseModel):
    posts: List[Post]

class ResponseComments(BaseModel):
    comments: List[Comment]

class ResponseMessages(BaseModel):
    messages: List[Message]
