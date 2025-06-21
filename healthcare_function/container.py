import logging

from dependency_injector import containers, providers

from app.config.mongodb.mongodb_client import MongoDBClient
from app.config.s3.s3_client import S3Client
from app.config.static_config import StaticConfig
from app.repository.common_repository import CommonRepository
from app.service.common_service import CommonService

logger = logging.getLogger(__name__)

class Container(containers.DeclarativeContainer):
    
    wiring_config = containers.WiringConfiguration(packages=[
        "app.config",
        "app.controller",
        "app.service",
    ])

    config = providers.Configuration()

    static_config = providers.Singleton(
        StaticConfig,
        app_args=config.app_args
    )

    mongodb_client = providers.Singleton(
        MongoDBClient,
        static_config
    )

    s3_client = providers.Singleton(
        S3Client,
        static_config
    )

    common_repository = providers.Singleton(
        CommonRepository,
        mongodb_client
    )

    common_service = providers.Singleton(
        CommonService,
        s3_client,
        common_repository
    )
