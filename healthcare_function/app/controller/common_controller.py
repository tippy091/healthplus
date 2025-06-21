import logging

from dependency_injector.wiring import Provide, inject
from flask import jsonify


from app.service.common_service import CommonService
from blueprint import api_bp
from container import Container


logger = logging.getLogger(__name__)
@api_bp.route("/health-check")
@inject
def health_check(common_service: CommonService = Provide[Container.common_service]):
    logger.info("I'm still alive")
    return jsonify(common_service.get_status()), 200


