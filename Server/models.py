# external packages
from pydantic import BaseModel
from typing import Optional, List
from enum import Enum


class ReactionsEnum(str, Enum):
    like = 'like'
    love = 'love'
    hate = 'hate'
    fuck_you = 'fuck you'


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

class RequestReaction(BaseModel):
    reaction: ReactionsEnum

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
    groups: list

class ResponseUserProfile(BaseModel):
    user_id: int
    username: str
    email: str
    avatar: Optional[bytes] = None

class ResponseCollegueProfile(BaseModel):
    user_id: int
    username: str
    avatar: Optional[bytes] = None

class ResponseGroup(BaseModel):
    name: str
    admin_id: int
    image: Optional[bytes] = None

class ResponseGroupUsers(BaseModel):
    users: list

class Post(BaseModel):
    post_id: int
    author_id: int
    created: int
    text: str

class ResponsePosts(BaseModel):
    posts: List[Post]

class Reaction(BaseModel):
    user_id: int
    reaction: ReactionsEnum

class ResponseReactions(BaseModel):
    reactions: List[Reaction]

class Comment(BaseModel):
    comment_id: int
    author_id: int
    created: int
    text: str

class ResponseComments(BaseModel):
    comments: List[Comment]

class Message(BaseModel):
    message_id: int
    author_id: int
    created: int
    text: str

class ResponseMessages(BaseModel):
    messages: List[Message]
